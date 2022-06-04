package com.example.isthisahangout.ui.detailsscreen

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentDetailDisplayBinding
import com.example.isthisahangout.models.AnimeGenreResults
import com.example.isthisahangout.models.favourites.FavAnime
import com.example.isthisahangout.viewmodel.FavouritesViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class DetailDisplayFragment : Fragment(R.layout.fragment_detail_display) {
    private var _binding: FragmentDetailDisplayBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailDisplayFragmentArgs>()
    @Inject
    lateinit var mAuth: FirebaseAuth
    private val viewModel by viewModels<FavouritesViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailDisplayBinding.bind(view)
        val content: AnimeGenreResults.AnimeByGenres = args.content
        binding.apply {

            Glide.with(requireContext())
                .load(content.imageUrl)
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
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.favAnime.collect {
                    val isFav = it.any { anime ->
                        anime.title == content.title
                    }
                    viewModel.isAnimeBookMarked.value = isFav
                }
            }
            bookmarkButton.setOnClickListener {
                viewModel.isAnimeBookMarked.value = !viewModel.isAnimeBookMarked.value
                if (!viewModel.isAnimeBookMarked.value) {
                    viewModel.deleteAnime(content.id.toInt())
                } else {
                    viewModel.addAnime(
                        FavAnime(
                            id = content.id.toInt(),
                            title = content.title,
                            image = content.imageUrl,
                            userId = mAuth.currentUser!!.uid
                        )
                    )
                }
            }
            titleTextView.text = content.title
            if (content.synopsis == "X") {
                synopsisTextView.isVisible = false
                synopsisTitleTextView.isVisible = false
            } else {
                synopsisTextView.text = content.synopsis
            }
            if (content.url == "X") {
                seeMoreTextView.isVisible = false
            } else {
                seeMoreTextView.apply {
                    paint.isUnderlineText = true
                    val uri = Uri.parse(content.url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    setOnClickListener {
                        context.startActivity(intent)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.isAnimeBookMarked.collectLatest {
                    if (it) {
                        bookmarkButton.setImageResource(R.drawable.bookmarked)
                    } else {
                        bookmarkButton.setImageResource(R.drawable.bookmark)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}