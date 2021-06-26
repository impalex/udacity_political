package com.example.android.politicalpreparedness.Base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel: ViewModel() {
    protected val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    protected val _isFailure = MutableLiveData(false)
    val isFailure: LiveData<Boolean>
        get() = _isFailure

    protected val _showToast = MutableSharedFlow<String>()
    val showToast: Flow<String>
        get() = _showToast

    protected val _showToastRes = MutableSharedFlow<Int>()
    val showToastRes: Flow<Int>
        get() = _showToastRes

    private val _openUrl = MutableSharedFlow<String>()
    val openUrl: Flow<String>
        get() = _openUrl

    fun openUrl(url: String) = viewModelScope.launch {
        _openUrl.emit(url)
    }
}