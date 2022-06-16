package com.example.isthisahangout.ui.detailsscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.PokemonMovesAdapter
import com.example.isthisahangout.databinding.FragmentPokemonDetailsBinding
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.PokemonViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.*

class PokemonDetailsFragment : Fragment(R.layout.fragment_pokemon_details) {
    private var _binding: FragmentPokemonDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<PokemonViewModel>()
    val args by navArgs<PokemonDetailsFragmentArgs>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPokemonDetailsBinding.bind(view)
        viewModel.setPokemonName(args.pokemon.name)
        binding.apply {
            val pokemonMovesAdapter = PokemonMovesAdapter()
            movesRecyclerView.apply {
                adapter = pokemonMovesAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.pokemonDetailsResult.collectLatest { result ->
                    if (result == null) return@collectLatest
                    when (result) {
                        is Resource.Error -> {
                            pokemonDetailsLayout.isVisible = false
                            progressBar.isVisible = false
                            errorTextView.isVisible = true
                            errorTextView.text = result.error?.localizedMessage
                                ?: getString(R.string.aw_snap_an_error_occurred)
                        }
                        is Resource.Loading -> {
                            pokemonDetailsLayout.isVisible = false
                            progressBar.isVisible = true
                            errorTextView.isVisible = false
                        }
                        is Resource.Success -> {
                            pokemonDetailsLayout.isVisible = true
                            progressBar.isVisible = false
                            errorTextView.isVisible = false
                            val pokemon = result.data
                            if (pokemon == null) {
                                pokemonDetailsLayout.isVisible = false
                                errorTextView.isVisible = true
                                errorTextView.text = getString(R.string.aw_snap_an_error_occurred)
                            } else {
                                nameTextView.text = pokemon.name.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                }
                                weightTextView.text = "Weight - ${pokemon.weight / 10} kg"
                                val moves = pokemon.moves.map {
                                    it.move.name
                                }
                                pokemonMovesAdapter.submitList(moves)
                                Glide.with(requireContext())
                                    .load(args.pokemon.image)
                                    .into(imageView)
                            }
                        }
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