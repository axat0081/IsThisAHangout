package com.example.isthisahangout.ui.detailsscreen

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.ScreenshotAdapter
import com.example.isthisahangout.databinding.FragmentGameDetailsBinding
import com.example.isthisahangout.models.favourites.FavGame
import com.example.isthisahangout.viewmodel.FavouritesViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class GameDetailsFragment : Fragment(R.layout.fragment_game_details) {
    private var _binding: FragmentGameDetailsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<GameDetailsFragmentArgs>()
    private val viewModel by viewModels<FavouritesViewModel>()
    @Inject
    lateinit var mAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGameDetailsBinding.bind(view)
        val game = args.game
        val screenshotAdapter = ScreenshotAdapter()
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.favGame.collect { favGame ->
                    val isFav = favGame.any {
                        it.title == game.name
                    }
                    if (isFav) {
                        addGameToFavButton.isClickable = false
                        addGameToFavButton.text = "Added to Favourites"
                    }
                }
            }
            Glide.with(requireContext())
                .load(game.imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        gameProgressBar.isVisible = false
                        gameTitleTextView.isVisible = false
                        gameRatingTextView.isVisible = false
                        gameGenresTextView.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        gameProgressBar.isVisible = false
                        gameTitleTextView.isVisible = true
                        gameRatingTextView.isVisible = true
                        gameGenresTextView.isVisible = true
                        return false
                    }
                }).into(gameImageView)
            gameTitleTextView.text = game.name
            gameRatingTextView.text = "Rating - ${game.rating}"
            var genres = ""
            for (s in game.genres) {
                if (genres.length > 0) genres += ","
                genres += s
            }

            gameGenresTextView.text = "Genres - ${genres}"
            gameScreenshotsRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = screenshotAdapter
                itemAnimator = null
            }
            screenshotAdapter.submitList(game.screenshots)
            addGameToFavButton.setOnClickListener {
                viewModel.addGame(
                    FavGame(
                        id = game.id,
                        title = game.name!!,
                        image = game.imageUrl!!,
                        userId = mAuth.currentUser!!.uid
                    )
                )
                addGameToFavButton.isClickable = false
                addGameToFavButton.text = "Added To Favourites"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}