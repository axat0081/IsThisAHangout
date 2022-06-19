package com.example.isthisahangout.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.SongAdapter
import com.example.isthisahangout.databinding.FragmentSongBinding
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.models.toSong
import com.example.isthisahangout.utils.Status
import com.example.isthisahangout.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song), SongAdapter.OnItemClickListener {
    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SongViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSongBinding.bind(view)

        binding.apply {
            uploadSongButton.setOnClickListener {
                findNavController().navigate(
                    SongFragmentDirections.actionSongFragmentToUploadSongFragment()
                )
            }
            val songAdapter = SongAdapter(this@SongFragment)
            songRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = songAdapter
                itemAnimator = null
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.mediaItems.collectLatest { result ->
                    when (result.status) {
                        Status.SUCCESS -> {
                            val songDtos = result.data
                            if (songDtos != null) {
                                songAdapter.submitList(songDtos.map { it.toSong() })
                            }
                            songProgressBar.isVisible = false
                            songErrorTextView.isVisible = false
                        }
                        Status.LOADING -> {
                            songProgressBar.isVisible = true
                            songErrorTextView.isVisible = false
                        }
                        Status.ERROR -> {
                            songProgressBar.isVisible = false
                            songErrorTextView.isVisible = true
                            songErrorTextView.text =
                                result.message ?: getString(R.string.aw_snap_an_error_occurred)
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(song: Song) {
       viewModel.playOrToggleSong(song)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}