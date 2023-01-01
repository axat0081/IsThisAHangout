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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}