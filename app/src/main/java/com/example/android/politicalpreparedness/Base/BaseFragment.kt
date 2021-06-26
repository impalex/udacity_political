package com.example.android.politicalpreparedness.Base

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseFragment : Fragment() {
    protected abstract val viewModel: BaseViewModel

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.showToast.collect { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
        lifecycleScope.launch {
            viewModel.showToastRes.collect { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
        lifecycleScope.launch {
            viewModel.openUrl.collect { openUrl(it) }
        }
    }

    private fun openUrl(url: String) =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}