package com.example.android.politicalpreparedness.representative.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ItemRepresentativeBinding
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter.ViewHolder
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter(private val listener: Listener): ListAdapter<Representative, ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), listener)

    interface Listener {
        fun onWebClick(item: Representative)
        fun onFacebookClick(item: Representative)
        fun onTwitterClick(item: Representative)
    }

    class ViewHolder(private val binding: ItemRepresentativeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Representative, listener: Listener) {
            binding.representative = item
            binding.webImage.setOnClickListener { listener.onWebClick(item) }
            binding.facebookImage.setOnClickListener { listener.onFacebookClick(item) }
            binding.twitterImage.setOnClickListener { listener.onTwitterClick(item) }
        }

        companion object {
            fun from(parent: ViewGroup) =
                ViewHolder(ItemRepresentativeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Representative>() {
        override fun areItemsTheSame(oldItem: Representative, newItem: Representative) = oldItem.official.name == newItem.official.name

        override fun areContentsTheSame(oldItem: Representative, newItem: Representative) = oldItem == newItem

    }

}
