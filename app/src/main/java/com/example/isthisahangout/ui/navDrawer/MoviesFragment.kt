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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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
                    moviesAdapter.loadStateFlow.collect { loadState ->
                        when (loadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                moviesSwipeRefreshLayout.isRefreshing = true
                            }
                            is LoadState.NotLoading -> {
                                moviesSwipeRefreshLayout.isRefreshing = false
                            }
                            is LoadState.Error -> {
                                moviesSwipeRefreshLayout.isRefreshing = false
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