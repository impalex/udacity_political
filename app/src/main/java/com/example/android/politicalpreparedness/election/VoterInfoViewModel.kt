package com.example.android.politicalpreparedness.election

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Base.BaseViewModel
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.repository.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val repository: ElectionRepository) : BaseViewModel() {

    val electionData = MutableSharedFlow<Pair<String, Int>?>(1)

    private val _voterInfo = electionData.map { loadVoterInfo(it?.first ?: return@map null, it.second) }
    val voterInfo = _voterInfo.filterNotNull().asLiveData()

    val administrationBody = _voterInfo.map { it?.state?.firstOrNull()?.electionAdministrationBody }.asLiveData()

    @ExperimentalCoroutinesApi
    val isSaved = electionData.filterNotNull().flatMapLatest {
        repository.getSavedElectionFlow(it.second)
    }.map { it != null }.asLiveData()

    private suspend fun loadVoterInfo(address: String, electionId: Int): VoterInfoResponse? {
        _isFailure.postValue(false)
        _isLoading.postValue(true)
        try {
            repository.getVoterInfo(address, electionId).let {
                when (it) {
                    is Result.Success -> return it.data
                    is Result.Failure -> {
                        _showToastRes.emit(R.string.data_not_available)
                        _isFailure.postValue(true)
                    }
                }
            }
            return null
        }
        finally {
            _isLoading.postValue(false)
        }
    }

    @ExperimentalCoroutinesApi
    fun toggleFollowing(election: Election) {
        if (isSaved.value == true) {
            viewModelScope.launch { repository.deleteElection(election) }
        } else {
            viewModelScope.launch { repository.saveElection(election) }
        }
    }
}