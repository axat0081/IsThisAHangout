package com.example.isthisahangout.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.SongAdapter
import com.example.isthisahangout.databinding.FragmentSongBinding
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.models.toSong
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song), SongAdapter.OnItemClickListener {
    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SongViewModel>()
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSongBinding.bind(view)

        binding.apply {
            val songAdapter = SongAdapter(this@SongFragment)
            songRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = songAdapter
                itemAnimator = null
            }
            songRetryButton.isVisible = false
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.songs.collect { result->
                        if(result == null) return@collect
                        songAdapter.submitList(result.data?.map {
                            it.toSong()
                        })
                        when(result){
                            is Resource.Loading -> {
                                songRefreshLayout.isRefreshing = true
                                songErrorTextView.isVisible = false
                            }
                            is Resource.Success -> {
                                songRefreshLayout.isRefreshing = false
                                songErrorTextView.isVisible = false
                            }
                            is Resource.Error -> {
                                songRefreshLayout.isRefreshing = false
                                songErrorTextView.isVisible = true
                                val message = result.error?.localizedMessage?: ""
                                songErrorTextView.text = "Error: $message"
                            }
                        }
                    }
                }
            }
            uploadSongButton.setOnClickListener {
                findNavController().navigate(
                    SongFragmentDirections.actionSongFragmentToUploadSongFragment()
                )
            }
        }
    }

    override fun onItemClick(song: Song) {
        val action = SongFragmentDirections.actionSongFragmentToSongDetailFragment(song)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}