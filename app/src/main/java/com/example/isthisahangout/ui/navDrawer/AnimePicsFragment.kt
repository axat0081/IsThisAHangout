package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.AnimePicsAdapter
import com.example.isthisahangout.databinding.FragmentAnimePicsBinding
import com.example.isthisahangout.viewmodel.AnimeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AnimePicsFragment : Fragment(R.layout.fragment_anime_pics) {
    private var _binding: FragmentAnimePicsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AnimeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimePicsBinding.bind(view)
        val animePicsAdapter = AnimePicsAdapter()
        binding.apply {
            animePicsRecyclerview.apply {
                adapter = animePicsAdapter
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.animePics.collect { animePics ->
                    animePicsAdapter.submitList(animePics)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}