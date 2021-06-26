package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.android.politicalpreparedness.Base.BaseFragment
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.getFacebook
import com.example.android.politicalpreparedness.network.models.getTwitter
import com.example.android.politicalpreparedness.network.models.getUrl
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.getViewModel
import com.example.android.politicalpreparedness.util.tag
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class RepresentativeFragment : BaseFragment() {

    override val viewModel by getViewModel<RepresentativeViewModel>()
    private val representativeListAdapter by lazy { createRepresentativesAdapter() }
    private val isLandscape by lazy { resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentRepresentativeBinding.inflate(inflater).apply {
            viewModel = this@RepresentativeFragment.viewModel
            lifecycleOwner = this@RepresentativeFragment
            state.adapter = getStatesAdapter()
            myRepresentativesList.adapter = representativeListAdapter
            if (!isLandscape) {
                myRepresentativesList.setOnTouchListener { _, event ->
                    representativeMotionLayout?.onTouchEvent(event)
                    return@setOnTouchListener false
                }
            }
            buttonLocation.setOnClickListener { getLocation() }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { if (it) hideKeyboard() }

        lifecycleScope.launch {
            viewModel.representatives.collect {
                representativeListAdapter.submitList(it)
            }
        }


        return binding.root

    }

    private fun createRepresentativesAdapter() = RepresentativeListAdapter(object : RepresentativeListAdapter.Listener {
        override fun onWebClick(item: Representative) {
            viewModel.openUrl(item.official.getUrl() ?: return)
        }

        override fun onFacebookClick(item: Representative) {
            val id = item.official.getFacebook()?.id ?: return
            viewModel.openUrl("https://www.facebook.com/$id")
        }

        override fun onTwitterClick(item: Representative) {
            val id = item.official.getTwitter()?.id ?: return
            viewModel.openUrl("https://www.twitter.com/$id")
        }

    })

    private fun getStatesAdapter() =
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.states)).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) getLocation() else {
            Snackbar.make(requireView(), R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        }
    }

    private fun checkLocationPermissions() = if (isPermissionGranted()) {
        true
    } else {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        false
    }

    private fun isPermissionGranted() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private val requestTurnOnLocationLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == Activity.RESULT_OK) getLocation(false)
    }

    private fun getLocation(resolve: Boolean = true) {
        if (!checkLocationPermissions())
            return

        lifecycleScope.launch {
            try {
                val settingsClient = LocationServices.getSettingsClient(requireContext())
                val locRequest = LocationRequest.create().apply { priority = LocationRequest.PRIORITY_LOW_POWER }

                try {
                    settingsClient.checkLocationSettings(LocationSettingsRequest.Builder().addLocationRequest(locRequest).build()).await()
                } catch (e: Exception) {
                    Log.e(tag(), "Error", e)
                    if (e is ResolvableApiException && resolve)
                        try {
                            requestTurnOnLocationLauncher.launch(IntentSenderRequest.Builder(e.resolution).build())
                            return@launch
                        } catch (e: IntentSender.SendIntentException) {
                            throw e
                        }
                    throw e
                }

                LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.await()!!.let {
                    Log.i(tag(), "Last known location $it")
                    geoCodeLocation(it).let { address ->
                        Log.i(tag(), "Address: $address")
                        viewModel.updateRepresentatives(address)
                    }
                }
            } catch (e: SecurityException) {
                Log.e(tag(), "Unable to get user location", e)
            } catch (e: Exception) {
                Log.e(tag(), "Something happened", e)
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}