package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.favourites.FavAnimeAdapter
import com.example.isthisahangout.adapter.favourites.FavGameAdapter
import com.example.isthisahangout.adapter.favourites.FavPostAdapter
import com.example.isthisahangout.adapter.favourites.FavVideosAdapter
import com.example.isthisahangout.databinding.FragmentFavouritesBinding
import com.example.isthisahangout.models.AnimeGenreResults
import com.example.isthisahangout.models.favourites.FavAnime
import com.example.isthisahangout.models.favourites.FavGame
import com.example.isthisahangout.models.favourites.FavPost
import com.example.isthisahangout.models.favourites.FavVideo
import com.example.isthisahangout.ui.detailsscreen.AnimeDetailFragmentDirections
import com.example.isthisahangout.utils.onQueryTextChanged
import com.example.isthisahangout.viewmodel.FavouritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavouritesFragment : Fragment(R.layout.fragment_favourites),
    FavAnimeAdapter.OnItemClickListener, FavVideosAdapter.OnItemClickListener,
    FavPostAdapter.OnItemClickListener, FavGameAdapter.OnItemClickListener {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FavouritesViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavouritesBinding.bind(view)
        val animeAdapter = FavAnimeAdapter(this)
        val videosAdapter = FavVideosAdapter(this)
        val postAdapter = FavPostAdapter(this)
        val gameAdapter = FavGameAdapter(this)
        binding.apply {
            animeSearchView.onQueryTextChanged {
                viewModel.favAnimeQuery.value = it
            }
            gameSearchView.onQueryTextChanged {
                viewModel.favGameQuery.value = it
            }
            videoSearchView.onQueryTextChanged {
                viewModel.favVideoQuery.value = it
            }
            postSearchView.onQueryTextChanged {
                viewModel.favPostQuery.value = it
            }
            animeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.favAnimeQuery.value = newText
                    return true
                }
            })
            gameSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.favGameQuery.value = newText
                    return true
                }
            })
            postSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.favPostQuery.value = newText
                    return true
                }
            })
            videoSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.favVideoQuery.value = newText
                    return true
                }
            })
            animeRecyclerview.apply {
                adapter = animeAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            gamesRecyclerview.apply {
                adapter = gameAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            videoRecyclerview.apply {
                adapter = videosAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            postsRecyclerview.apply {
                adapter = postAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.favAnime.collect {
                    animeAdapter.submitList(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.favGame.collect {
                    gameAdapter.submitList(it)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.favVideo.collect {
                    videosAdapter.submitList(it)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.favPost.collect {
                    postAdapter.submitList(it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(anime: FavAnime) {
        val action = AnimeDetailFragmentDirections.actionGlobalAnimeDetailFragment(
            animeId = anime.id,
            animeName = anime.title
        )
        findNavController().navigate(action)
    }

    override fun onItemClick(anime: FavVideo) {

    }

    override fun onItemClick(anime: FavPost) {

    }

    override fun onItemClick(anime: FavGame) {

    }
}