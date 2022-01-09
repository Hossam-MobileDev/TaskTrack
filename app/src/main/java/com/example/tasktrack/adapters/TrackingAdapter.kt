package com.androidTask.trackingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktrack.database.Track
import com.example.tasktrack.databinding.ListItemTrackBinding
import com.example.tasktrack.ui.fragments.HistoryFragment
import com.example.tasktrack.ui.fragments.HistoryFragmentDirections


class TrackingAdapter  : ListAdapter<Track, RecyclerView.ViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TrackViewHolder(
            ListItemTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val track = getItem(position)
        (holder as TrackViewHolder).bind(track)
    }

    class TrackViewHolder(
        private val binding: ListItemTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.track?.let { track ->
                    navigateToPlant(track, it)
                }
            }
        }

        private fun navigateToPlant(
            track: Track,
            view: View
        ) {


            val direction =

                    HistoryFragmentDirections.actionHistoryFragmentToDetailsFragment(track)


            if (direction != null) {
                view.findNavController().navigate(direction)
            }
        }

        fun bind(item: Track) {
            binding.apply {
                track = item
                executePendingBindings()
            }
        }
    }
}

private class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {

    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}
