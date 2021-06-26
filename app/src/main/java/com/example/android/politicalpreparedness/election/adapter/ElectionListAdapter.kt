package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ItemElectionBinding
//import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val listener: Listener) : ListAdapter<Election, ElectionListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), listener)

    interface Listener {
        fun onClick(item: Election)
    }

    class ViewHolder(private val binding: ItemElectionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Election, listener: Listener) {
            binding.election = item
            binding.electionItemLayout.setOnClickListener { listener.onClick(item) }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) =
                ViewHolder(ItemElectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Election, newItem: Election) = oldItem == newItem

    }

}
