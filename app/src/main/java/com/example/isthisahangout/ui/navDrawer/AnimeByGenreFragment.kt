package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.GeneralLoadStateAdapter
import com.example.isthisahangout.adapter.anime.AnimePagingAdapter
import com.example.isthisahangout.databinding.FragmentAnimeByGenreBinding
import com.example.isthisahangout.ui.models.AnimeUIModel
import com.example.isthisahangout.viewmodel.AnimeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimeByGenreFragment : Fragment(R.layout.fragment_anime_by_genre),
    AnimePagingAdapter.OnItemClickListener {
    private var _binding: FragmentAnimeByGenreBinding? = null
    private val binding get() = _binding!!
    private val animeViewModel by viewModels<AnimeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeByGenreBinding.bind(view)
        val animeAdapter = AnimePagingAdapter(this)
        binding.apply {
            animeByGenreRecyclerView.apply {
                adapter = animeAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { animeAdapter.retry() },
                    footer = GeneralLoadStateAdapter { animeAdapter.retry() }
                )
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            viewLifecycleOwner.lifecycleScope.launch {
                animeViewModel.animeByGenre.collect {
                    animeAdapter.submitData(lifecycle = viewLifecycleOwner.lifecycle, it)
                }
            }
            actionChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("action")
            }
            shoujoChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Shoujo")
            }
            shonenChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Shounen")
            }
            adventureChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Adventure")
            }
            mysteryChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Mystery")
            }
            fantasyChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Fantasy")
            }
            comedyChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Comedy")
            }
            horrorChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Horror")
            }
            magicChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Magic")
            }
            mechaChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Mecha")
            }
            romanceChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Romance")
            }
            musicChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Music")
            }
            sciFiChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Sci Fi")
            }
            psychologicalChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Psychological")
            }
            sliceOfLifeChip.setOnClickListener {
                animeViewModel.searchAnimeByGenre("Slice Of Life")
            }

        }
    }

    override fun onItemClick(animeResults: AnimeUIModel.AnimeModel) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}