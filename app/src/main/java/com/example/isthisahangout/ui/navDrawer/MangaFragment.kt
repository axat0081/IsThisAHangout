package com.example.isthisahangout.ui.navDrawer

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
import com.example.isthisahangout.adapter.manga.MangaPagingAdapter
import com.example.isthisahangout.databinding.FragmentMangaBinding
import com.example.isthisahangout.ui.detailsscreen.MangaDetailFragmentDirections
import com.example.isthisahangout.ui.models.MangaUIModel
import com.example.isthisahangout.viewmodel.MangaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class MangaFragment : Fragment(R.layout.fragment_manga), MangaPagingAdapter.OnItemClickListener {
    private var _binding: FragmentMangaBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MangaViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMangaBinding.bind(view)
        val mangaAdapter = MangaPagingAdapter(this)
        val mangaByGenreAdapter = MangaPagingAdapter(this)
        binding.apply {

            mangaRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = mangaAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { mangaAdapter.retry() },
                    footer = GeneralLoadStateAdapter { mangaAdapter.retry() }
                )
            }

            mangaByGenreRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = mangaByGenreAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { mangaByGenreAdapter.retry() },
                    footer = GeneralLoadStateAdapter { mangaByGenreAdapter.retry() }
                )
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.manga.collect {
                    mangaAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.mangaByGenre.collect {
                    mangaByGenreAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mangaAdapter.loadStateFlow
                        .distinctUntilChangedBy { it.source.refresh }
                        .filter { it.source.refresh is LoadState.NotLoading }
                        .collect {
                            if (viewModel.mangaPendingScrollToTop && it.mediator?.refresh is LoadState.NotLoading) {
                                mangaRecyclerView.scrollToPosition(0)
                                viewModel.mangaPendingScrollToTop = false
                            }
                        }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mangaByGenreAdapter.loadStateFlow
                        .distinctUntilChangedBy { it.source.refresh }
                        .filter { it.source.refresh is LoadState.NotLoading }
                        .collect {
                            if (viewModel.mangaByGenrePendingScrollToTop && it.mediator?.refresh is LoadState.NotLoading) {
                                mangaByGenreRecyclerView.scrollToPosition(0)
                                viewModel.mangaByGenrePendingScrollToTop = false
                            }
                        }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mangaAdapter.loadStateFlow.collect { combinedLoadState ->
                        when (val refresh = combinedLoadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                mangaProgressBar.isVisible = true
                                mangaErrorTextView.isVisible = false
                                mangaRetryButton.isVisible = false
                                viewModel.mangaPendingScrollToTop = true
                            }
                            is LoadState.NotLoading -> {
                                mangaProgressBar.isVisible = false
                                mangaErrorTextView.isVisible = false
                                mangaRetryButton.isVisible = false
                            }
                            is LoadState.Error -> {
                                mangaProgressBar.isVisible = false
                                mangaErrorTextView.isVisible = true
                                mangaRetryButton.isVisible = true
                                val errorMessage =
                                    "Aw snap an error occurred $refresh.error.localizedMessage ?:\"\" "
                                mangaErrorTextView.text = errorMessage
                                viewModel.mangaPendingScrollToTop = false
                            }
                            null -> Unit
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mangaByGenreAdapter.loadStateFlow.collect { combinedLoadState ->
                        when (val refresh = combinedLoadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                mangaByGenreProgressBar.isVisible = true
                                mangaByGenreErrorTextView.isVisible = false
                                mangaByGenreRetryButton.isVisible = false
                                viewModel.mangaByGenrePendingScrollToTop = true
                            }
                            is LoadState.NotLoading -> {
                                mangaByGenreProgressBar.isVisible = false
                                mangaByGenreErrorTextView.isVisible = false
                                mangaByGenreRetryButton.isVisible = false
                            }
                            is LoadState.Error -> {
                                mangaByGenreProgressBar.isVisible = false
                                mangaByGenreErrorTextView.isVisible = true
                                mangaByGenreRetryButton.isVisible = true
                                val errorMessage =
                                    "Aw snap an error occurred $refresh.error.localizedMessage ?:\"\" "
                                mangaByGenreErrorTextView.text = errorMessage
                                viewModel.mangaByGenrePendingScrollToTop = false
                            }
                            null -> Unit
                        }
                    }
                }
            }

            mangaRetryButton.setOnClickListener {
                mangaAdapter.retry()
            }

            mangaByGenreRetryButton.setOnClickListener {
                mangaByGenreAdapter.retry()
            }

            actionChip.setOnClickListener {
                viewModel.searchMangaByGenre("action")
            }
            shoujoChip.setOnClickListener {
                viewModel.searchMangaByGenre("Shoujo")
            }
            shonenChip.setOnClickListener {
                viewModel.searchMangaByGenre("Shounen")
            }
            adventureChip.setOnClickListener {
                viewModel.searchMangaByGenre("Adventure")
            }
            mysteryChip.setOnClickListener {
                viewModel.searchMangaByGenre("Mystery")
            }
            fantasyChip.setOnClickListener {
                viewModel.searchMangaByGenre("Fantasy")
            }
            comedyChip.setOnClickListener {
                viewModel.searchMangaByGenre("Comedy")
            }
            horrorChip.setOnClickListener {
                viewModel.searchMangaByGenre("Horror")
            }
            magicChip.setOnClickListener {
                viewModel.searchMangaByGenre("Magic")
            }
            mechaChip.setOnClickListener {
                viewModel.searchMangaByGenre("Mecha")
            }
            romanceChip.setOnClickListener {
                viewModel.searchMangaByGenre("Romance")
            }
            musicChip.setOnClickListener {
                viewModel.searchMangaByGenre("Music")
            }
            sciFiChip.setOnClickListener {
                viewModel.searchMangaByGenre("Sci Fi")
            }
            psychologicalChip.setOnClickListener {
                viewModel.searchMangaByGenre("Psychological")
            }
            sliceOfLifeChip.setOnClickListener {
                viewModel.searchMangaByGenre("Slice Of Life")
            }
        }
    }

    override fun onItemClick(mangaResults: MangaUIModel.MangaModel) {
        val action = MangaDetailFragmentDirections.actionGlobalMangaDetailFragment(
            mangaId = mangaResults.id,
            mangaName = mangaResults.title
        )
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}