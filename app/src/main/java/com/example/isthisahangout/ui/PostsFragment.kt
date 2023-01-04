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
import com.example.isthisahangout.adapter.PostsPagingAdapter
import com.example.isthisahangout.databinding.FragmentPostsBinding
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.utils.startAnimation
import com.example.isthisahangout.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.fragment_posts), PostsPagingAdapter.OnItemClickListener{
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PostViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostsBinding.bind(view)
        val postsPagingAdapter = PostsPagingAdapter(this)
        val animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }
        binding.apply {
            postsRecyclerview.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = postsPagingAdapter
                itemAnimator = null
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.postsFlow.collectLatest {
                        postsPagingAdapter.submitData(it)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    postsPagingAdapter.loadStateFlow.collect { loadState ->
                        swipeRefreshLayout.isRefreshing =
                            loadState.source.refresh is LoadState.Loading
                        postsErrorTextView.isVisible = loadState.source.refresh is LoadState.Error
                        postsRetryButton.isVisible = loadState.source.refresh is LoadState.Error
                    }
                }
            }

            postsRetryButton.setOnClickListener {
                postsPagingAdapter.retry()
            }

            createPostsButton.setOnClickListener {
                createPostsButton.isVisible = false
                circleBackground.isVisible = true
                circleBackground.startAnimation(animation) {
                    binding.circleBackground.isVisible = false
                    findNavController().navigate(
                        PostsFragmentDirections.actionPostsFragment2ToCreatePostFragment()
                    )
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                postsPagingAdapter.refresh()
            }
        }
    }

    override fun onItemClickPaged(post: FirebasePost) {
        findNavController().navigate(
            PostsFragmentDirections.actionPostsFragment2ToPostsDetailsFragment2(
                FirebasePost(
                    id = post.id!!,
                    pfp = post.pfp,
                    image = post.image,
                    time = post.time,
                    likes = post.likes ?: 0,
                    text = post.text,
                    username = post.username,
                    title = post.title,
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}