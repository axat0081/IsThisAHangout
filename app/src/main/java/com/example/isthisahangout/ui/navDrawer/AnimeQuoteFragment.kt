package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.AnimeQuotesAdapter
import com.example.isthisahangout.databinding.FragmentAnimeQuotesBinding
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.AnimeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AnimeQuoteFragment : Fragment(R.layout.fragment_anime_quotes) {
    private var _binding: FragmentAnimeQuotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AnimeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeQuotesBinding.bind(view)
        val quoteAdapter = AnimeQuotesAdapter()
        binding.apply {
            animeQuotesRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = quoteAdapter
                itemAnimator = null
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.animeQuotes.collect {
                    val result = it ?: return@collect
                    swipeRefreshLayout.isRefreshing = result is Resource.Loading
                    quoteErrorTextView.isVisible =
                        result.error != null && result.data.isNullOrEmpty()
                    quoteRetryButton.isVisible = result.error != null && result.data.isNullOrEmpty()
                    quoteAdapter.submitList(result.data)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.quoteEventFlow.collect { event ->
                    when (event) {
                        is AnimeViewModel.QuoteEvent.ShowErrorMessage -> {
                            Snackbar.make(
                                requireView(),
                                event.error.localizedMessage,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.onQuoteManualRefresh()
            }
            quoteRetryButton.setOnClickListener {
                viewModel.onQuoteManualRefresh()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onQuoteStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}