package com.example.android.politicalpreparedness.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.repository.ElectionRepository

inline fun <reified VM : ViewModel> Fragment.getViewModel() =
    viewModels<VM> { ViewModelFactory.getInstance(ElectionRepository.getInstance(requireContext())) }