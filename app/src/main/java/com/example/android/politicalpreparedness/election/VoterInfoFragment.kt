package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.Base.BaseFragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.asAddressString
import com.example.android.politicalpreparedness.util.getViewModel
import kotlinx.coroutines.launch

class VoterInfoFragment : BaseFragment() {

    override val viewModel by getViewModel<VoterInfoViewModel>()
    private val args by navArgs<VoterInfoFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentVoterInfoBinding.inflate(inflater).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
        setHasOptionsMenu(true)
        updateElectionData()

        return binding.root
    }

    private fun updateElectionData() = lifecycleScope.launch {
        viewModel.electionData.emit(Pair(args.argDivision.asAddressString(), args.argElectionId))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.refresh_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_refresh -> {
            updateElectionData()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}