package com.example.isthisahangout.ui.navDrawer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.GeneralLoadStateAdapter
import com.example.isthisahangout.adapter.anime.AnimePagingAdapter
import com.example.isthisahangout.databinding.FragmentAnimeByGenreSeasonBinding
import com.example.isthisahangout.ui.models.AnimeUIModel
import com.example.isthisahangout.viewmodel.AnimeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimeByGenreFragment : Fragment(R.layout.fragment_anime_by_genre_season),
    AnimePagingAdapter.OnItemClickListener {
    private var _binding: FragmentAnimeByGenreSeasonBinding? = null
    private val binding get() = _binding!!
    private val animeViewModel by viewModels<AnimeViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeByGenreSeasonBinding.bind(view)
        val animeGenreAdapter = AnimePagingAdapter(this)
        val animeSeasonAdapter = AnimePagingAdapter(this)
        binding.apply {
            animeByGenreRecyclerView.apply {
                adapter = animeGenreAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { animeGenreAdapter.retry() },
                    footer = GeneralLoadStateAdapter { animeGenreAdapter.retry() }
                )
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            animeBySeasonsRecyclerView.apply {
                adapter = animeSeasonAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { animeGenreAdapter.retry() },
                    footer = GeneralLoadStateAdapter { animeGenreAdapter.retry() }
                )
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            viewLifecycleOwner.lifecycleScope.launch {
                animeViewModel.animeByGenre.collect {
                    animeGenreAdapter.submitData(lifecycle = viewLifecycleOwner.lifecycle, it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                    animeViewModel.animeBySeason.collectLatest {
                        animeSeasonAdapter.submitData(it)
                    }
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

            summerChip.setOnClickListener {
                animeViewModel.searchAnimeBySeason("summer")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            winterChip.setOnClickListener {
                animeViewModel.searchAnimeBySeason("winter")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            fallChip.setOnClickListener {
                animeViewModel.searchAnimeBySeason("fall")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            springChip.setOnClickListener {
                animeViewModel.searchAnimeBySeason("spring")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            ZeroChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2020")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            NineChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2019")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            EightChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2018")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            SevenChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2017")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            SixChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2016")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            fiveChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2015")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            fourChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2014")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            threeChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2013")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            twoChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2012")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            oneChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2011")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            }
            oneZeroChip.setOnClickListener {
                animeViewModel.searchAnimeByYear("2010")
                seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
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