package com.example.isthisahangout.ui.detailsscreen

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentAnimeDetailBinding
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.detailScreen.AnimeDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeDetailFragment : Fragment(R.layout.fragment_anime_detail) {
    private var _binding: FragmentAnimeDetailBinding? = null
    private val binding: FragmentAnimeDetailBinding get() = _binding!!
    private val viewModel by viewModels<AnimeDetailViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeDetailBinding.bind(view)
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.animeDetail.collect { result ->
                        if (result == null) return@collect
                        when (result) {
                            is Resource.Loading -> {
                                animeDetailsProgressBar.isVisible = true
                            }
                            is Resource.Success -> {
                                animeDetailsProgressBar.isVisible = false
                                val anime = result.data
                                if (anime == null) {
                                    Snackbar.make(
                                        requireView(),
                                        "Aw snap an error occurred",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    return@collect
                                }
                                titleTextView.text = anime.title
                                animeGenreTextView.text = anime.genres
                                synopsisTextView.text = anime.synopsis
                                ratingTextView.text = "Rating - ${anime.rating}"
                                favoritesTextView.text = "Favourites: ${anime.favorites}"
                                Glide.with(requireContext())
                                    .load(anime.image)
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            imageProgressBar.isVisible = false
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            imageProgressBar.isVisible = false
                                            return false
                                        }

                                    }).into(detailsImageView)
                                if (anime.trailerId != null) {
                                    viewLifecycleOwner.lifecycle.addObserver(trailerView)
                                    trailerView.addYouTubePlayerListener(object :
                                        AbstractYouTubePlayerListener() {
                                        override fun onReady(youTubePlayer: YouTubePlayer) {
                                            super.onReady(youTubePlayer)
                                            youTubePlayer.loadVideo(anime.trailerId, 0.0F)
                                        }
                                    })
                                } else {
                                    trailerView.isVisible = false
                                    trailerTextView.text = "Trailer - N/A"
                                }
                            }
                            is Resource.Error -> {
                                animeDetailsProgressBar.isVisible = false
                                Snackbar.make(
                                    requireView(),
                                    result.error?.localizedMessage ?: "Aw snap an error occurred",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isFavAnime.collect { isFav ->
                        bookmarkButton.setImageResource(
                            if (isFav) R.drawable.bookmarked
                            else R.drawable.bookmark
                        )
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.bookMarkEventFlow.collect { event ->
                        when (event) {
                            AnimeDetailViewModel.AnimeBookMarkEvent.AddedToBookMarks -> {
                                Snackbar.make(
                                    requireView(),
                                    "Added to bookmarks",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            AnimeDetailViewModel.AnimeBookMarkEvent.RemovedFromBookMarks -> {
                                Snackbar.make(
                                    requireView(),
                                    "Removed from bookmarks",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

            bookmarkButton.setOnClickListener {
                viewModel.onBookMarkClick()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}