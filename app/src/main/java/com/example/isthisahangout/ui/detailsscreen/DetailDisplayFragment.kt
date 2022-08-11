package com.example.isthisahangout.ui.detailsscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentDetailDisplayBinding
import com.example.isthisahangout.viewmodel.FavouritesViewModel
import com.example.isthisahangout.viewmodel.detailScreen.AnimeDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class DetailDisplayFragment : Fragment(R.layout.fragment_detail_display) {
    private var _binding: FragmentDetailDisplayBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var mAuth: FirebaseAuth
    private val viewModel by viewModels<FavouritesViewModel>()
    private val animeDetailViewModel by viewModels<AnimeDetailViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailDisplayBinding.bind(view)
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    animeDetailViewModel.animeDetail.collectLatest { result ->
                        if (result == null) return@collectLatest
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