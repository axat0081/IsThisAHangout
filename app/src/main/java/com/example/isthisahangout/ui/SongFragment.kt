package com.example.isthisahangout.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.SongAdapter
import com.example.isthisahangout.adapter.SongRecyclerAdapter
import com.example.isthisahangout.databinding.FragmentSongBinding
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song), SongAdapter.OnItemClickListener,
    SongRecyclerAdapter.OnItemClickListener {
    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SongViewModel>()
    private lateinit var songRecyclerAdapter: SongRecyclerAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSongBinding.bind(view)
        val songAdapter = SongAdapter(this)
        songRecyclerAdapter = SongRecyclerAdapter(this)
        val concatAdapter = ConcatAdapter(
            songRecyclerAdapter,
            songAdapter
        )
        binding.apply {
            uploadSongButton.setOnClickListener {
                findNavController().navigate(
                    SongFragmentDirections.actionSongFragmentToUploadSongFragment()
                )
            }
            songRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = concatAdapter
                itemAnimator = null
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.songs.collect { songs ->
                    songAdapter.submitData(viewLifecycleOwner.lifecycle, songs)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                songAdapter.loadStateFlow.collect { loadState ->
                    songProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
                    songErrorTextView.isVisible = loadState.source.refresh is LoadState.Error
                    songRetryButton.isVisible = loadState.source.refresh is LoadState.Error
                }
            }
            songRetryButton.setOnClickListener {
                songAdapter.retry()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        songRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        songRecyclerAdapter.stopListening()
    }

    override fun onItemClick(song: Song) {
        findNavController().navigate(
            SongFragmentDirections.actionSongFragmentToSongDetailFragment(song)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}