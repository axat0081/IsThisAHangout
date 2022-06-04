package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.GeneralLoadStateAdapter
import com.example.isthisahangout.adapter.MangaAdapter
import com.example.isthisahangout.adapter.MangaByGenreAdapter
import com.example.isthisahangout.databinding.FragmentMangaBinding
import com.example.isthisahangout.models.AnimeGenreResults
import com.example.isthisahangout.models.MangaResults
import com.example.isthisahangout.models.RoomMangaByGenre
import com.example.isthisahangout.viewmodel.MangaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MangaFragment : Fragment(R.layout.fragment_manga), MangaByGenreAdapter.OnItemClickListener,
    MangaAdapter.OnItemClickListener {
    private var _binding: FragmentMangaBinding? = null
    private val binding get() = _binding!!
    private val mangaViewModel by viewModels<MangaViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMangaBinding.bind(view)
        val mangaAdapter = MangaAdapter(this)
        val mangaByGenreAdapter = MangaByGenreAdapter(this)
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
                mangaViewModel.manga.collect {
                    mangaAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                mangaViewModel.mangaByGenre.collect {
                    mangaByGenreAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                mangaAdapter.loadStateFlow.collect { loadState ->
                    mangaProgressBar.isVisible =
                        loadState.source.refresh is LoadState.Loading
                    mangaErrorTextView.isVisible =
                        loadState.source.refresh is LoadState.Error
                    mangaRetryButton.isVisible = loadState.source.refresh is LoadState.Error
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                mangaByGenreAdapter.loadStateFlow.collect { loadState ->
                    mangaByGenreProgressBar.isVisible =
                        loadState.source.refresh is LoadState.Loading
                    mangaByGenreErrorTextView.isVisible =
                        loadState.source.refresh is LoadState.Error
                    mangaByGenreRetryButton.isVisible = loadState.source.refresh is LoadState.Error
                }
            }

            mangaByGenreRetryButton.setOnClickListener {
                mangaByGenreAdapter.retry()
            }

            mangaRetryButton.setOnClickListener {
                mangaAdapter.retry()
            }

            actionChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("action")
            }
            shoujoChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Shoujo")
            }
            shonenChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Shounen")
            }
            adventureChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Adventure")
            }
            mysteryChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Mystery")
            }
            fantasyChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Fantasy")
            }
            comedyChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Comedy")
            }
            horrorChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Horror")
            }
            magicChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Magic")
            }
            mechaChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Mecha")
            }
            romanceChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Romance")
            }
            musicChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Music")
            }
            sciFiChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Sci Fi")
            }
            psychologicalChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Psychological")
            }
            sliceOfLifeChip.setOnClickListener {
                mangaViewModel.searchMangaByGenre("Slice Of Life")
            }
        }
    }

    override fun onItemClick(manga: MangaResults.Manga) {
        findNavController().navigate(
            MangaFragmentDirections.actionMangaFragmentToDetailDisplayFragment(
                AnimeGenreResults.AnimeByGenres(
                    id = manga.id,
                    title = manga.title,
                    imageUrl = manga.imageUrl,
                    synopsis = "X",
                    url = manga.url
                )
            )
        )
    }

    override fun onItemClick(manga: RoomMangaByGenre) {

        findNavController().navigate(
            MangaFragmentDirections.actionMangaFragmentToDetailDisplayFragment(
                AnimeGenreResults.AnimeByGenres(
                    id = manga.id,
                    title = manga.title,
                    imageUrl = manga.imageUrl,
                    synopsis = manga.synopsis,
                    url = manga.url
                )
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}