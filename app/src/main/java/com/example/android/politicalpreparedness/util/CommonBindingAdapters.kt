package com.example.android.politicalpreparedness.util

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("boolVisibility")
fun View.setBoolVisibility(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("objectVisibility")
fun View.setObjectVisibility(value: Any?) {
    visibility = if (value == null) View.GONE else View.VISIBLE
}