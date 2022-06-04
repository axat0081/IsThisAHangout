package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.AnimeByNameAdapter
import com.example.isthisahangout.databinding.FragmentAnimeByNameBinding
import com.example.isthisahangout.models.AnimeByNameResults
import com.example.isthisahangout.models.AnimeGenreResults
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.AnimeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AnimeByNameFragment : Fragment(R.layout.fragment_anime_by_name),
    AnimeByNameAdapter.OnItemClickListener {
    private var _binding: FragmentAnimeByNameBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AnimeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeByNameBinding.bind(view)
        val animeByNameAdapter = AnimeByNameAdapter(this)
        binding.apply {
            animeNameEditText.addTextChangedListener { text ->
                viewModel.animeNameText = text.toString()
            }
            searchButton.setOnClickListener {
                viewModel.searchAnimeByNameClick()
            }
            animeByNameRecyclerview.apply {
                adapter = animeByNameAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.animeByName.collect { result ->
                    animeByNameAdapter.submitList(result.data)
                    progressBar.isVisible =
                        result is Resource.Loading && result.data.isNullOrEmpty()
                    errorTextView.isVisible =
                        result is Resource.Error && result.data.isNullOrEmpty()
                    errorTextView.text = result.error?.localizedMessage
                }
            }
        }
    }

    override fun onItemClick(anime: AnimeByNameResults.AnimeByName) {
        findNavController().navigate(
            AnimeByNameFragmentDirections.actionAnimeByNameFragmentToDetailDisplayFragment(
                AnimeGenreResults.AnimeByGenres(
                    title = anime.title,
                    imageUrl = anime.imageUrl,
                    synopsis = anime.synopsis,
                    url = anime.url,
                    id = anime.id
                )
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}