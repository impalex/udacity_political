package com.example.android.politicalpreparedness.representative.adapter

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Official
import com.example.android.politicalpreparedness.network.models.getFacebook
import com.example.android.politicalpreparedness.network.models.getTwitter
import com.example.android.politicalpreparedness.network.models.getUrl

@BindingAdapter("profileImage")
fun ImageView.fetchImage(src: String?) {
    src?.let {
        val uri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(this)
    } ?: setImageResource(R.drawable.ic_profile)
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

@Suppress("unused")
@InverseBindingAdapter(attribute = "stateValue")
fun Spinner.getNewValue() = selectedItem?.toString() ?: ""

@Suppress("unused")
@BindingAdapter("stateValueAttrChanged")
fun Spinner.setSpinnerListeners(attrChange: InverseBindingListener?) {
    attrChange?.let {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                it.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                it.onChange()
            }
        }
    }
}

@BindingAdapter("facebook")
fun ImageView.setFacebook(official: Official) {
    visibility = if (official.getFacebook() != null) View.VISIBLE else View.GONE
}

@BindingAdapter("twitter")
fun ImageView.setTwitter(official: Official) {
    visibility = if (official.getTwitter() != null) View.VISIBLE else View.GONE
}

@BindingAdapter("url")
fun ImageView.setUrl(official: Official) {
    visibility = if (official.getUrl() != null) View.VISIBLE else View.GONE
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}
