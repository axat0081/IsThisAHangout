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
import com.example.isthisahangout.databinding.FragmentMangaDetailBinding
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.detailScreen.MangaDetailViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MangaDetailFragment : Fragment(R.layout.fragment_manga_detail) {
    private var _binding: FragmentMangaDetailBinding? = null
    private val binding: FragmentMangaDetailBinding get() = _binding!!
    private val viewModel by viewModels<MangaDetailViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMangaDetailBinding.bind(view)
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.mangaDetail.collect { result ->
                        if (result == null) return@collect
                        val data = result.data
                        data?.let { manga ->
                            titleTextView.text = manga.title
                            synopsisTextView.text = manga.synopsis
                            ratingTextView.text = "Rating - ${manga.rating}"
                            favoritesTextView.text = "Favourites - ${manga.favorites}"
                            Glide.with(requireView())
                                .load(manga.image)
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
                        }
                        when (result) {
                            is Resource.Loading -> {
                                mangaDetailsProgressBar.isVisible = true
                                errorTextView.isVisible = false
                            }
                            is Resource.Success -> {
                                mangaDetailsProgressBar.isVisible = false
                                errorTextView.isVisible = false
                            }
                            is Resource.Error -> {
                                mangaDetailsProgressBar.isVisible = false
                                errorTextView.isVisible = true
                                val errorMessage =
                                    result.error?.localizedMessage ?: "Aw snap an error occurred"
                                errorTextView.text = errorMessage
                            }
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isFav.collect { isFav ->
                        bookmarkButton.setImageResource(
                            if (isFav) R.drawable.bookmarked
                            else R.drawable.bookmark
                        )
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.mangaBookMarkEventFlow.collect { event ->
                        when (event) {
                            MangaDetailViewModel.MangaBookMarkEvent.AddedToBookMarks -> {
                                Snackbar.make(
                                    requireView(),
                                    "Added to bookmarks",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            MangaDetailViewModel.MangaBookMarkEvent.RemovedFromBookMarks -> {
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