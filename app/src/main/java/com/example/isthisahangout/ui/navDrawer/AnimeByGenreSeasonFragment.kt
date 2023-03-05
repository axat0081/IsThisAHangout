package com.example.isthisahangout.ui.navDrawer

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.GeneralLoadStateAdapter
import com.example.isthisahangout.adapter.anime.AnimePagingAdapter
import com.example.isthisahangout.databinding.FragmentAnimeByGenreSeasonBinding
import com.example.isthisahangout.ui.detailsscreen.AnimeDetailFragmentDirections
import com.example.isthisahangout.ui.models.AnimeUIModel
import com.example.isthisahangout.viewmodel.AnimeBySeasonGenreViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class AnimeByGenreSeasonFragment : Fragment(R.layout.fragment_anime_by_genre_season),
    AnimePagingAdapter.OnItemClickListener {
    private var _binding: FragmentAnimeByGenreSeasonBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AnimeBySeasonGenreViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeByGenreSeasonBinding.bind(view)
        val animeByGenreAdapter = AnimePagingAdapter(this)
        val animeBySeasonAdapter = AnimePagingAdapter(this)
        binding.apply {
            animeByGenreRecyclerView.apply {
                adapter = animeByGenreAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { animeByGenreAdapter.retry() },
                    footer = GeneralLoadStateAdapter { animeByGenreAdapter.retry() }
                )
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            animeBySeasonsRecyclerView.apply {
                adapter = animeBySeasonAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { animeBySeasonAdapter.retry() },
                    footer = GeneralLoadStateAdapter { animeBySeasonAdapter.retry() }
                )
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.animeByGenre.collectLatest {
                        animeByGenreAdapter.submitData(it)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.animeBySeason.collectLatest {
                        animeBySeasonAdapter.submitData(it)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    animeByGenreAdapter.loadStateFlow
                        .distinctUntilChangedBy { it.source.refresh }
                        .filter { it.source.refresh is LoadState.NotLoading }
                        .collect {
                            if (viewModel.animeByGenrePendingScrollToTop && it.mediator?.refresh is LoadState.NotLoading) {
                                animeByGenreRecyclerView.scrollToPosition(0)
                                viewModel.animeByGenrePendingScrollToTop = false
                            }
                        }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    animeBySeasonAdapter.loadStateFlow
                        .distinctUntilChangedBy { it.source.refresh }
                        .filter { it.source.refresh is LoadState.NotLoading }
                        .collect {
                            if (viewModel.animeBySeasonPendingScrollToTop && it.mediator?.refresh is LoadState.NotLoading) {
                                animeBySeasonsRecyclerView.scrollToPosition(0)
                                viewModel.animeBySeasonPendingScrollToTop = false
                            }
                        }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    animeByGenreAdapter.loadStateFlow.collect { combinedLoadState ->
                        when (val refresh = combinedLoadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                animeByGenreProgressBar.isVisible = true
                                animeByGenreErrorTextView.isVisible = false
                                animeByGenreRetryButton.isVisible = false
                                viewModel.animeByGenreRefreshInProgress = true
                                viewModel.animeByGenrePendingScrollToTop = true
                            }
                            is LoadState.NotLoading -> {
                                animeByGenreProgressBar.isVisible = false
                                animeByGenreErrorTextView.isVisible = false
                                animeByGenreRetryButton.isVisible = false
                                viewModel.animeByGenreRefreshInProgress = false
                            }
                            is LoadState.Error -> {
                                animeByGenreProgressBar.isVisible = false
                                animeByGenreErrorTextView.isVisible = true
                                animeByGenreRetryButton.isVisible = true

                                val errorMessage =
                                    "Ah snap an error occurred: $refresh.error.localizedMessage ?:\"\""
                                animeByGenreErrorTextView.text = errorMessage
                                if (viewModel.animeByGenreRefreshInProgress) {
                                    Snackbar.make(
                                        requireView(),
                                        errorMessage,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                viewModel.animeByGenreRefreshInProgress = false
                                viewModel.animeByGenrePendingScrollToTop = false
                            }
                            null -> Unit
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    animeBySeasonAdapter.loadStateFlow.collect { combinedLoadState ->
                        when (val refresh = combinedLoadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                animeBySeasonsProgressBar.isVisible = true
                                animeBySeasonsErrorTextView.isVisible = false
                                animeBySeasonsRetryButton.isVisible = false
                                viewModel.animeBySeasonRefreshInProgress = true
                                viewModel.animeBySeasonPendingScrollToTop = true
                            }
                            is LoadState.NotLoading -> {
                                animeBySeasonsProgressBar.isVisible = false
                                animeBySeasonsErrorTextView.isVisible = false
                                animeBySeasonsRetryButton.isVisible = false
                                viewModel.animeBySeasonRefreshInProgress = false
                            }
                            is LoadState.Error -> {
                                animeBySeasonsProgressBar.isVisible = false
                                animeBySeasonsErrorTextView.isVisible = true
                                animeBySeasonsRetryButton.isVisible = true

                                val errorMessage =
                                    "Ah snap an error occurred: $refresh.error.localizedMessage ?:\"\""
                                animeBySeasonsErrorTextView.text = errorMessage
                                if (viewModel.animeBySeasonRefreshInProgress) {
                                    Snackbar.make(
                                        requireView(),
                                        errorMessage,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                viewModel.animeBySeasonRefreshInProgress = false
                                viewModel.animeBySeasonPendingScrollToTop = false
                            }
                            null -> Unit
                        }
                    }
                }
            }

            animeByGenreRetryButton.setOnClickListener {
                animeByGenreAdapter.retry()
            }

            animeBySeasonsRetryButton.setOnClickListener {
                animeBySeasonAdapter.retry()
            }

            actionChip.setOnClickListener {
                viewModel.searchAnimeByGenre("action")
            }
            shoujoChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Shoujo")
            }
            shonenChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Shounen")
            }
            adventureChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Adventure")
            }
            mysteryChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Mystery")
            }
            fantasyChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Fantasy")
            }
            comedyChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Comedy")
            }
            horrorChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Horror")
            }
            magicChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Magic")
            }
            mechaChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Mecha")
            }
            romanceChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Romance")
            }
            musicChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Music")
            }
            sciFiChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Sci Fi")
            }
            psychologicalChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Psychological")
            }
            sliceOfLifeChip.setOnClickListener {
                viewModel.searchAnimeByGenre("Slice Of Life")
            }

            summerChip.setOnClickListener {
                viewModel.searchAnimeBySeason("summer")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            winterChip.setOnClickListener {
                viewModel.searchAnimeBySeason("winter")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            fallChip.setOnClickListener {
                viewModel.searchAnimeBySeason("fall")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            springChip.setOnClickListener {
                viewModel.searchAnimeBySeason("spring")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            ZeroChip.setOnClickListener {
                viewModel.searchAnimeByYear("2020")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            NineChip.setOnClickListener {
                viewModel.searchAnimeByYear("2019")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            EightChip.setOnClickListener {
                viewModel.searchAnimeByYear("2018")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            SevenChip.setOnClickListener {
                viewModel.searchAnimeByYear("2017")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            SixChip.setOnClickListener {
                viewModel.searchAnimeByYear("2016")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            fiveChip.setOnClickListener {
                viewModel.searchAnimeByYear("2015")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            fourChip.setOnClickListener {
                viewModel.searchAnimeByYear("2014")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            threeChip.setOnClickListener {
                viewModel.searchAnimeByYear("2013")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            twoChip.setOnClickListener {
                viewModel.searchAnimeByYear("2012")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            oneChip.setOnClickListener {
                viewModel.searchAnimeByYear("2011")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
            oneZeroChip.setOnClickListener {
                viewModel.searchAnimeByYear("2010")
                seasonTextView.text = viewModel.season.value + " " + viewModel.year.value
            }
        }
    }

    override fun onItemClick(animeResults: AnimeUIModel.AnimeModel) {
        val action = AnimeDetailFragmentDirections.actionGlobalAnimeDetailFragment(
            animeId = animeResults.id,
            animeName = animeResults.title
        )
        findNavController().navigate(action)
    }

    override fun onAnimeLikeClick(animeResults: AnimeUIModel.AnimeModel) {
        viewModel.onAnimeLikeClick(animeResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}