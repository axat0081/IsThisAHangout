package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.PokemonAdapter
import com.example.isthisahangout.adapter.VerticalLoadStateAdapter
import com.example.isthisahangout.databinding.FragmentPokemonBinding
import com.example.isthisahangout.models.pokemon.Pokemon
import com.example.isthisahangout.viewmodel.PokemonViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PokemonFragment : Fragment(R.layout.fragment_pokemon), PokemonAdapter.OnItemClickListener {
    private var _binding: FragmentPokemonBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PokemonViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPokemonBinding.bind(view)
        binding.apply {
            val pokemonAdapter = PokemonAdapter(this@PokemonFragment)
            pokemonRecyclerview.apply {
                adapter = pokemonAdapter.withLoadStateHeaderAndFooter(
                    header = VerticalLoadStateAdapter { pokemonAdapter.retry() },
                    footer = VerticalLoadStateAdapter { pokemonAdapter.retry() }
                )
                itemAnimator = null
                layoutManager = GridLayoutManager(requireContext(), 2)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.pokemon.collectLatest { data ->
                    pokemonAdapter.submitData(data)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                pokemonAdapter.loadStateFlow
                    .collect { loadState ->
                        when (loadState.mediator?.refresh) {
                            is LoadState.NotLoading -> {
                                pokemonRecyclerview.isVisible = true
                                pokemonProgressBar.isVisible = false
                                pokemonErrorTextView.isVisible = false
                                pokemonRetryButton.isVisible = false
                                viewModel.refreshInProgress = false
                            }
                            is LoadState.Error -> {
                                pokemonRecyclerview.isVisible = false
                                pokemonProgressBar.isVisible = false
                                pokemonErrorTextView.isVisible = true
                                pokemonRetryButton.isVisible = true
                                val errorMessage =
                                    (loadState.mediator?.refresh as LoadState.Error)
                                        .error.localizedMessage
                                        ?: getString(R.string.aw_snap_an_error_occurred)
                                pokemonErrorTextView.text = errorMessage
                                if(viewModel.refreshInProgress){
                                    Snackbar.make(
                                        requireView(),
                                        errorMessage,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    viewModel.refreshInProgress = false
                                }
                            }
                            is LoadState.Loading -> {
                                pokemonRecyclerview.isVisible = false
                                pokemonProgressBar.isVisible = true
                                pokemonErrorTextView.isVisible = false
                                pokemonRetryButton.isVisible = false
                                viewModel.refreshInProgress = true
                            }
                            null -> Unit
                        }
                    }
            }
            pokemonRetryButton.setOnClickListener {
                pokemonAdapter.retry()
            }
        }
    }

    override fun onItemClick(pokemon: Pokemon) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}