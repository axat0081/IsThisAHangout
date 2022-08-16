package com.example.isthisahangout.ui.detailsscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
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
import com.example.isthisahangout.databinding.FragmentDetailDisplayBinding
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.detailScreen.AnimeDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailDisplayFragment : Fragment(R.layout.fragment_detail_display) {
    private var _binding: FragmentDetailDisplayBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AnimeDetailViewModel>()
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailDisplayBinding.bind(view)
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.animeDetail.collectLatest { result ->
                        if (result == null) return@collectLatest
                        val anime = result.data
                        if (anime != null) {
                            titleTextView.text = anime.title
                            animeGenreTextView.text = anime.genres
                            synopsisTextView.text = anime.synopsis
                            ratingTextView.text = "Rating - ${anime.rating ?: "NA"}"
                            seeMoreTextView.paint.isUnderlineText = true
                            seeMoreTextView.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(anime.url))
                                startActivity(intent)
                            }
                            Glide.with(requireContext())
                                .load(anime.image)
                                .addListener(object : RequestListener<Drawable> {
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
                        animeDetailsProgressBar.isVisible = result is Resource.Loading
                        errorTextView.isVisible = result is Resource.Error
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isFavAnime.collectLatest { isFav ->
                        bookmarkButton.setImageResource(
                            if (isFav) R.drawable.bookmarked else R.drawable.bookmark
                        )
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