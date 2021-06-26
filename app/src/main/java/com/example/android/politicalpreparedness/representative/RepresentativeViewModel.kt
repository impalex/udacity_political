package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Base.BaseViewModel
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.repository.Result
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val repository: ElectionRepository) : BaseViewModel() {

    private val _representatives = MutableStateFlow<List<Representative>?>(null)
    val representatives: Flow<List<Representative>?>
        get() = _representatives

    val addressLine1 = MutableLiveData("")
    val addressLine2 = MutableLiveData("")
    val city = MutableLiveData("")
    val zip = MutableLiveData("")
    val state = MutableLiveData("")

    fun updateRepresentatives(address: Address) {
        address.let {
            addressLine1.value = it.line1
            addressLine2.value = it.line2
            city.value = it.city
            zip.value = it.zip
            state.value = it.state
        }
        updateRepresentatives()
    }

    fun updateRepresentatives() {
        listOf(addressLine1, addressLine2, city, state, zip).map { it.value }.filter { !it.isNullOrBlank() }.joinToString(" ")
            .let { fetchRepresentatives(it) }
    }

    private fun fetchRepresentatives(address: String) = viewModelScope.launch {
        if (address.isBlank()) {
            _showToastRes.emit(R.string.err_invalid_address)
            return@launch
        }
        try {
            _isLoading.postValue(true)
            _isFailure.postValue(false)
            repository.getRepresentativeInfo(address).let {
                when (it) {
                    is Result.Success -> _representatives.emit(getRepresentatives(it.data))
                    is Result.Failure -> {
                        _showToastRes.emit(R.string.data_not_available)
                        _isFailure.postValue(true)
                    }
                }
            }
        } finally {
            _isLoading.postValue(false)
        }
    }

    private fun getRepresentatives(representativeResponse: RepresentativeResponse) =
        representativeResponse.offices.flatMap {
            it.getRepresentatives(representativeResponse.officials)
        }

}
