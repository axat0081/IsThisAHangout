package com.example.isthisahangout.ui.navDrawer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.AnimeSeasonAdapter
import com.example.isthisahangout.databinding.FragmentAnimeSeasonBinding
import com.example.isthisahangout.models.AnimeGenreResults
import com.example.isthisahangout.models.AnimeSeasonResults
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.AnimeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeSeasonFragment : Fragment(R.layout.fragment_anime_season),
    AnimeSeasonAdapter.OnItemClickListener {
    private var _binding: FragmentAnimeSeasonBinding? = null
    private val binding get() = _binding!!
    private val animeViewModel by viewModels<AnimeViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeSeasonBinding.bind(view)
        val animeSeasonAdapter = AnimeSeasonAdapter(this)
        binding.apply {
            seasonTextView.text = animeViewModel.season.value + " " + animeViewModel.year.value
            seasonRecyclerView.apply {
                adapter = animeSeasonAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                itemAnimator = null
            }
            swipeRefreshLayout.setOnRefreshListener {
                animeViewModel.onManualRefresh()
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                animeViewModel.animeBySeason.collect {
                    val result = it ?: return@collect
                    swipeRefreshLayout.isRefreshing = result is Resource.Loading
                    seasonErrorTextView.isVisible =
                        result.error != null && result.data.isNullOrEmpty()
                    seasonRetryButton.isVisible =
                        result.error != null && result.data.isNullOrEmpty()
                    seasonErrorTextView.text = getString(
                        R.string.could_not_refresh,
                        result.error?.localizedMessage
                            ?: getString(R.string.unknown_error_occurred)
                    )

                    animeSeasonAdapter.submitList(result.data) {
                        if (animeViewModel.pendingScrollToTopAfterRefresh) {
                            seasonRecyclerView.scrollToPosition(0)
                            animeViewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                animeViewModel.seasonEvents.collect { event ->
                    when (event) {
                        is AnimeViewModel.Event.ShowErrorMessage -> {
                            Snackbar.make(
                                requireView(),
                                event.error.localizedMessage
                                    ?: getString(R.string.aw_snap_an_error_occurred),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            seasonRetryButton.setOnClickListener {
                animeViewModel.onManualRefresh()
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
        animeSeasonAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

    }

    override fun onItemClick(anime: AnimeSeasonResults.RoomAnimeBySeason) {

    }

    override fun onStart() {
        super.onStart()
        animeViewModel.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}