package com.example.isthisahangout.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.VideosPagingAdapter
import com.example.isthisahangout.adapter.VideosRecyclerAdapter
import com.example.isthisahangout.databinding.FragmentVideosBinding
import com.example.isthisahangout.models.FirebaseVideo
import com.example.isthisahangout.utils.startAnimation
import com.example.isthisahangout.viewmodel.VideoViewModel
import com.google.android.exoplayer2.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class VideosFragment : Fragment(R.layout.fragment_videos), VideosPagingAdapter.OnItemClickListener,
    VideosRecyclerAdapter.OnItemClickListener {
    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoViewModel>()
    private lateinit var videosRecyclerAdapter: VideosRecyclerAdapter
    private lateinit var videosPagingAdapter: VideosPagingAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVideosBinding.bind(view)
        videosPagingAdapter = VideosPagingAdapter(this)
        videosRecyclerAdapter = VideosRecyclerAdapter(this)
        concatAdapter = ConcatAdapter(
            videosPagingAdapter,
            videosRecyclerAdapter
        )
        val animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }
        binding.apply {
            postVideoButton.setOnClickListener {
                binding.postVideoButton.isVisible = false
                circleBackground.isVisible = true
                circleBackground.startAnimation(animation) {
                    circleBackground.isVisible = false
                    findNavController().navigate(
                        VideosFragmentDirections.actionVideosFragment2ToUploadVideoFragment()
                    )
                }
            }

            videosRecyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = concatAdapter
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.videos.collect { pagedVideos ->
                    videosPagingAdapter.submitData(viewLifecycleOwner.lifecycle, pagedVideos)
                }
            }

            videosRetryButton.setOnClickListener {
                videosPagingAdapter.retry()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        videosRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        videosRecyclerAdapter.stopListening()
    }

    override fun onItemClick(video: FirebaseVideo) {
        findNavController().navigate(
            VideosFragmentDirections.actionVideosFragment2ToVideoDetailsFragment(video)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}