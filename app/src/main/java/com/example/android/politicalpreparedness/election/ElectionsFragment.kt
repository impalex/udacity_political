package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.Base.BaseFragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.getViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ElectionsFragment : BaseFragment() {

    override val viewModel by getViewModel<ElectionsViewModel>()
    private val upcomingElectionsAdapter by lazy { createElectionsAdapter() }
    private val savedElectionsAdapter by lazy { createElectionsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentElectionBinding.inflate(inflater).apply {
            viewModel = this@ElectionsFragment.viewModel
            lifecycleOwner = this@ElectionsFragment
            upcomingElectionsList.adapter = upcomingElectionsAdapter
            savedElectionsList.adapter = savedElectionsAdapter
        }
        setHasOptionsMenu(true)

        setupCollectors()

        return binding.root
    }

    private fun createElectionsAdapter() = ElectionListAdapter(object : ElectionListAdapter.Listener {
        override fun onClick(item: Election) {
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(item.id, item.division))
        }
    })

    private fun setupCollectors() {
        lifecycleScope.launch {
            viewModel.upcomingElections.collect { upcomingElectionsAdapter.submitList(it) }
        }
        lifecycleScope.launch {
            viewModel.savedElections.collect { savedElectionsAdapter.submitList(it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.refresh_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshElections()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}