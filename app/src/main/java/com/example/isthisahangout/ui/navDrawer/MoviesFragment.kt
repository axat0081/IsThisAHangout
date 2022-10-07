package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.VerticalLoadStateAdapter
import com.example.isthisahangout.adapter.movies.MoviesAdapter
import com.example.isthisahangout.databinding.FragmentMoviesBinding
import com.example.isthisahangout.models.movies.Movie
import com.example.isthisahangout.viewmodel.MoviesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class MoviesFragment : Fragment(R.layout.fragment_movies), MoviesAdapter.OnItemClickListener {
    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MoviesViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMoviesBinding.bind(view)
        val moviesAdapter = MoviesAdapter(this)
        binding.apply {

            moviesRecyclerview.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = moviesAdapter.withLoadStateHeaderAndFooter(
                    header = VerticalLoadStateAdapter { moviesAdapter.retry() },
                    footer = VerticalLoadStateAdapter { moviesAdapter.retry() }
                )
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.movies.collectLatest { movies ->
                        moviesAdapter.submitData(movies)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    moviesAdapter.loadStateFlow
                        .distinctUntilChangedBy { it.source.refresh }
                        .filter { it.source.refresh is LoadState.NotLoading }
                        .collect {
                            if (viewModel.pendingScrollToTop && it.mediator?.refresh is LoadState.NotLoading) {
                                moviesRecyclerview.scrollToPosition(0)
                                viewModel.pendingScrollToTop = false
                            }
                        }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    moviesAdapter.loadStateFlow.collect { loadState ->
                        when (val refresh = loadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                moviesSwipeRefreshLayout.isRefreshing = true
                                viewModel.refreshInProgress = true
                                viewModel.pendingScrollToTop = true
                            }
                            is LoadState.NotLoading -> {
                                moviesSwipeRefreshLayout.isRefreshing = false
                                viewModel.refreshInProgress = false
                            }
                            is LoadState.Error -> {
                                moviesSwipeRefreshLayout.isRefreshing = false
                                if (viewModel.refreshInProgress) {
                                    val errorMessage =
                                        refresh.error.localizedMessage
                                            ?: "An unknown error occurred"
                                    Snackbar.make(
                                        requireView(),
                                        errorMessage,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                viewModel.refreshInProgress = false
                                viewModel.pendingScrollToTop = false
                            }
                            null -> Unit
                        }
                    }
                }
            }

            moviesSwipeRefreshLayout.setOnRefreshListener {
                moviesAdapter.refresh()
            }
        }
    }

    override fun onItemClick(movie: Movie) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}