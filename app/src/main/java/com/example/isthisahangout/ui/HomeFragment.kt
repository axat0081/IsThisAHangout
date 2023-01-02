package com.example.isthisahangout.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.GeneralLoadStateAdapter
import com.example.isthisahangout.adapter.anime.AnimePagingAdapter
import com.example.isthisahangout.adapter.videoGame.VideoGamesPagingAdapter
import com.example.isthisahangout.databinding.FragmentHomeBinding
import com.example.isthisahangout.ui.detailsscreen.AnimeDetailFragmentDirections
import com.example.isthisahangout.ui.models.AnimeUIModel
import com.example.isthisahangout.ui.models.VideoGameUIModel
import com.example.isthisahangout.viewmodel.AnimeViewModel
import com.example.isthisahangout.viewmodel.FirebaseAuthViewModel
import com.example.isthisahangout.viewmodel.GameViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), AnimePagingAdapter.OnItemClickListener,
    VideoGamesPagingAdapter.OnItemClickListener {

    @Inject
    lateinit var mAuth: FirebaseAuth
    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!
    private val animeViewModel by viewModels<AnimeViewModel>()
    private val gamesViewModel by viewModels<GameViewModel>()
    private val viewModel by activityViewModels<FirebaseAuthViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mAuth.currentUser == null) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment2, true)
                .build()
            findNavController()
                .navigate(
                    HomeFragmentDirections.actionHomeFragment2ToLoginFragment(),
                    navOptions
                )
            return
        } else {
            viewModel.updateUserData()
        }
        _binding = FragmentHomeBinding.bind(view)
        val upcomingAnimeAdapter = AnimePagingAdapter(this)
        val airingAnimeAdapter = AnimePagingAdapter(this)
        val gamesAdapter = VideoGamesPagingAdapter(this)
        binding.apply {
            upcomingAnimeRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = upcomingAnimeAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { upcomingAnimeAdapter.retry() },
                    footer = GeneralLoadStateAdapter { upcomingAnimeAdapter.retry() }
                )
                itemAnimator = null
            }
            airingAnimeRecyclerview.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = airingAnimeAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { airingAnimeAdapter.retry() },
                    footer = GeneralLoadStateAdapter { airingAnimeAdapter.retry() }
                )
                itemAnimator = null
            }
            videoGamesRecyclerview.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = gamesAdapter.withLoadStateHeaderAndFooter(
                    header = GeneralLoadStateAdapter { gamesAdapter.retry() },
                    footer = GeneralLoadStateAdapter { gamesAdapter.retry() }
                )
                itemAnimator = null
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    animeViewModel.upcomingAnime.collectLatest {
                        upcomingAnimeErrorTextView.isVisible = false
                        upcomingAnimeProgressBar.isVisible = false
                        upcomingAnimeRetryBtn.isVisible = false
                        upcomingAnimeAdapter.submitData(it)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    upcomingAnimeAdapter.loadStateFlow.collect { loadState ->
                        when (loadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                upcomingAnimeProgressBar.isVisible = true
                                upcomingAnimeRetryBtn.isVisible = false
                                upcomingAnimeErrorTextView.isVisible = false
                            }
                            is LoadState.NotLoading -> {
                                upcomingAnimeProgressBar.isVisible = false
                                upcomingAnimeRetryBtn.isVisible = false
                                upcomingAnimeErrorTextView.isVisible = false
                                airingAnimeTextView.isVisible
                            }
                            is LoadState.Error -> {
                                upcomingAnimeProgressBar.isVisible = false
                                upcomingAnimeRetryBtn.isVisible = true
                                upcomingAnimeErrorTextView.isVisible = true
                            }
                            else -> Unit
                        }
                    }
                }
            }
            upcomingAnimeRetryBtn.setOnClickListener {
                upcomingAnimeAdapter.retry()
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    animeViewModel.airingAnime.collectLatest {
                        airingAnimeAdapter.submitData(it)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    airingAnimeAdapter.loadStateFlow.collect { loadState ->
                        when (loadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                airingAnimeProgressBar.isVisible = true
                                airingAnimeRetryButton.isVisible = false
                                airingAnimeErrorTextView.isVisible = false
                            }
                            is LoadState.NotLoading -> {
                                airingAnimeProgressBar.isVisible = false
                                airingAnimeRetryButton.isVisible = false
                                airingAnimeErrorTextView.isVisible = false
                            }
                            is LoadState.Error -> {
                                airingAnimeProgressBar.isVisible = false
                                airingAnimeRetryButton.isVisible = true
                                airingAnimeErrorTextView.isVisible = true
                            }
                            null -> Unit
                        }
                    }
                }
            }
            airingAnimeRetryButton.setOnClickListener {
                airingAnimeAdapter.retry()
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    gamesViewModel.games.collectLatest {
                        gamesAdapter.submitData(it)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    gamesAdapter.loadStateFlow.collect { loadState ->
                        when (loadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                videoGamesProgressBar.isVisible = true
                                videoGamesNoResultsTxt.isVisible = false
                                videoGamesRetryBtn.isVisible = false
                            }
                            is LoadState.NotLoading -> {
                                videoGamesProgressBar.isVisible = false
                                videoGamesNoResultsTxt.isVisible = false
                                videoGamesRetryBtn.isVisible = false
                            }
                            is LoadState.Error -> {
                                videoGamesProgressBar.isVisible = false
                                videoGamesNoResultsTxt.isVisible = true
                                videoGamesRetryBtn.isVisible = true
                            }
                            null -> Unit
                        }
                    }
                }
            }
            videoGamesRetryBtn.setOnClickListener {
                gamesAdapter.retry()
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_top_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                mAuth.signOut()
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragment2ToLoginFragment()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(animeResults: AnimeUIModel.AnimeModel) {
        val action = AnimeDetailFragmentDirections.actionGlobalAnimeDetailFragment(
            animeResults.id,
            animeResults.title
        )
        findNavController().navigate(action)
    }

    override fun onItemClick(videoGame: VideoGameUIModel.VideoGameModel) {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}