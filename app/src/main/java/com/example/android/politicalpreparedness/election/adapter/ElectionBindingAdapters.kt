package com.example.android.politicalpreparedness.election.adapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("date")
fun TextView.setDate(date: Date?) {
    text = when (date) {
        null -> ""
        else -> SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US).format(date)
    }
}

@BindingAdapter("followState")
fun Button.setFollowState(isFollowing: Boolean?) {
    text = context.getString(if (isFollowing == true) R.string.unfollow else R.string.follow)
}

@BindingAdapter("addressString")
fun TextView.setAddressString(address: Address?) {
    address.let {
        visibility = if (it == null) View.GONE else View.VISIBLE
        text = it?.toFormattedString() ?: ""
    }
}