package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.AnimeByDayAdapter
import com.example.isthisahangout.databinding.FragmentAnimeScheduleBinding
import com.example.isthisahangout.models.AnimeGenreResults
import com.example.isthisahangout.models.RoomAnimeByDay
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.AnimeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AnimeScheduleFragment : Fragment(R.layout.fragment_anime_schedule),
    AnimeByDayAdapter.OnItemClickListener {
    private var _binding: FragmentAnimeScheduleBinding? = null
    private val binding get() = _binding!!
    private val animeViewModel by viewModels<AnimeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeScheduleBinding.bind(view)
        val animeAdapter = AnimeByDayAdapter(this)
        binding.apply {
            animeByDayRecyclerView.apply {
                adapter = animeAdapter
                layoutManager =
                    GridLayoutManager(requireContext(), 2)
            }
            mondayChip.setOnClickListener {
                animeViewModel.searchAnimeByDay("monday")
            }
            tuesdayChip.setOnClickListener {
                animeViewModel.searchAnimeByDay("tuesday")
            }
            wednesdayChip.setOnClickListener {
                animeViewModel.searchAnimeByDay("wednesday")
            }
            thursdayChip.setOnClickListener {
                animeViewModel.searchAnimeByDay("thursday")
            }
            fridayChip.setOnClickListener {
                animeViewModel.searchAnimeByDay("friday")
            }
            saturdayChip.setOnClickListener {
                animeViewModel.searchAnimeByDay("saturday")
            }
            sundayChip.setOnClickListener {
                animeViewModel.searchAnimeByDay("sunday")
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                animeViewModel.animeByDay.collect { result ->
                    if (result == null) return@collect
                    animeProgressBar.isVisible = result is Resource.Loading
                    animeErrorTextView.isVisible = result is Resource.Error
                    animeAdapter.submitList(result.data)
                }
            }
        }
    }

    override fun onItemClick(anime: RoomAnimeByDay) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}