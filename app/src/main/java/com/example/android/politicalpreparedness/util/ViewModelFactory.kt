package com.example.android.politicalpreparedness.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val repository: ElectionRepository): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ElectionsViewModel::class.java) -> ElectionsViewModel(repository)
            modelClass.isAssignableFrom(VoterInfoViewModel::class.java) -> VoterInfoViewModel(repository)
            modelClass.isAssignableFrom(RepresentativeViewModel::class.java) -> RepresentativeViewModel(repository)
            else -> throw IllegalArgumentException("Invalid ViewModel class")
        } as T

    companion object {
        private lateinit var INSTANCE: ViewModelFactory
        fun getInstance(repository: ElectionRepository): ViewModelFactory {
            synchronized(this) {
                if (!::INSTANCE.isInitialized)
                    INSTANCE = ViewModelFactory(repository)
            }
            return INSTANCE
        }
    }
}