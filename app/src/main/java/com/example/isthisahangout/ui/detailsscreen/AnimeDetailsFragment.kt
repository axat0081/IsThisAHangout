package com.example.isthisahangout.ui.detailsscreen

import android.graphics.drawable.Drawable
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
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentAnimeDetailBinding
import com.example.isthisahangout.models.favourites.FavAnime
import com.example.isthisahangout.viewmodel.FavouritesViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class AnimeDetailsFragment : Fragment(R.layout.fragment_anime_detail) {
    private var _binding: FragmentAnimeDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<AnimeDetailsFragmentArgs>()
    private val viewModel by viewModels<FavouritesViewModel>()
    @Inject
    lateinit var mAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeDetailBinding.bind(view)
        val anime = args.anime
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.favAnime.collect { favAnime ->
                    val isFav = favAnime.any {
                        it.title == anime.title
                    }
                    if (isFav) {
                        addAnimeToFavButton.isClickable = false
                        addAnimeToFavButton.text = "Added to Favourites"
                    }
                }
            }
            Glide.with(requireContext())
                .load(anime.imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        animeTitleTextView.isVisible = false
                        animeProgressBar.isVisible = false
                        animeStartDateTextView.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        animeTitleTextView.isVisible = true
                        animeProgressBar.isVisible = false
                        animeStartDateTextView.isVisible = true
                        return false
                    }
                })
                .into(animeImageView)
            animeTitleTextView.text = anime.title
            animeStartDateTextView.text = "Start Date: ${anime.startDate}"
            addAnimeToFavButton.setOnClickListener {
                viewModel.addAnime(
                    FavAnime(
                        title = anime.title,
                        image = anime.imageUrl,
                        userId = mAuth.currentUser!!.uid
                    )
                )
                addAnimeToFavButton.isClickable = false
                addAnimeToFavButton.text = "Added to Favourites"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}