package com.example.isthisahangout.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.VideosPagingAdapter
import com.example.isthisahangout.databinding.FragmentVideosBinding
import com.example.isthisahangout.models.FirebaseVideo
import com.example.isthisahangout.utils.startAnimation
import com.example.isthisahangout.viewmodel.VideoViewModel
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.master.exoplayer.MasterExoPlayerHelper
import com.master.exoplayer.MuteStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class VideosFragment : Fragment(R.layout.fragment_videos), VideosPagingAdapter.OnItemClickListener {
    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVideosBinding.bind(view)
        val videosPagingAdapter = VideosPagingAdapter(this)
        val animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }
        val videoPlayerHelper = MasterExoPlayerHelper(
            mContext = requireContext(),
            id = R.id.masterExoPlayer,
            defaultMute = true,
            muteStrategy = MuteStrategy.ALL
        )
        videoPlayerHelper.makeLifeCycleAware(this)
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
                adapter = videosPagingAdapter
            }
            videoPlayerHelper.attachToRecyclerView(videosRecyclerview)
            videoPlayerHelper.getPlayerView().apply {
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
            parentFragmentManager.addOnBackStackChangedListener {
                if (parentFragmentManager.fragments.last() == this@VideosFragment) {
                    //resume
                    videoPlayerHelper.exoPlayerHelper.play()
                } else {
                    //pause
                    videoPlayerHelper.exoPlayerHelper.pause()
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.videos.collectLatest { pagedVideos ->
                        videosPagingAdapter.submitData(pagedVideos)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    videosPagingAdapter.loadStateFlow.collect { loadState ->
                        swipeRefreshLayout.isRefreshing =
                            loadState.source.refresh is LoadState.Loading
                        videosErrorTextView.isVisible = loadState.source.refresh is LoadState.Error
                        videosRetryButton.isVisible = loadState.source.refresh is LoadState.Error

                    }
                }
            }

            videosRetryButton.setOnClickListener {
                videosPagingAdapter.retry()
            }

            swipeRefreshLayout.setOnRefreshListener {
                videosPagingAdapter.refresh()
            }
        }
    }

    override fun onItemClick(video: FirebaseVideo) {
        findNavController().navigate(
            VideosFragmentDirections.actionVideosFragment2ToVideoDetailsFragment(video)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}