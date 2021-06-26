package com.example.android.politicalpreparedness.election

import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Base.BaseViewModel
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.repository.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ElectionsViewModel(private val repository: ElectionRepository) : BaseViewModel() {

    val savedElections = repository.getSavedElections()

    private val _upcomingElections = MutableStateFlow(listOf<Election>())
    val upcomingElections: Flow<List<Election>>
        get() = _upcomingElections

    init {
        refreshElections()
    }

    fun refreshElections() = viewModelScope.launch {
        _isFailure.postValue(false)
        _isLoading.postValue(true)
        try {
            repository.getUpcomingElections().let {
                when (it) {
                    is Result.Success -> _upcomingElections.emit(it.data)
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
}