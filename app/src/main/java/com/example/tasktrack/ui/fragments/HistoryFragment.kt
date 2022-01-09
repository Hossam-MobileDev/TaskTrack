package com.example.tasktrack.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.androidTask.trackingapp.adapters.TrackingAdapter
import com.example.tasktrack.R
import com.example.tasktrack.databinding.FragmentHistoryBinding
import com.example.tasktrack.databinding.FragmentTrackBinding
import com.example.tasktrack.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()

    lateinit var binding: FragmentHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_history, container, false
        )

        val adapter = TrackingAdapter()
        binding.historyList.adapter = adapter
        subscribeUi(adapter)
        return binding.root
    }

    private fun subscribeUi(adapter: TrackingAdapter) {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.submitList(tracks)
        }
    }

}