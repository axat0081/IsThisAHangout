package com.example.isthisahangout.ui.navDrawer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.AnimeNewsAdapter
import com.example.isthisahangout.databinding.FragmentAnimeNewsBinding
import com.example.isthisahangout.models.AnimeNews
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.AnimeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AnimeNewsFragment : Fragment(R.layout.fragment_anime_news),
    AnimeNewsAdapter.OnItemClickListener {
    private var _binding: FragmentAnimeNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AnimeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnimeNewsBinding.bind(view)
        val animeNewsAdapter = AnimeNewsAdapter(this)
        binding.apply {
            animeNewsRecyclerView.apply {
                adapter = animeNewsAdapter
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.animeNews.collect { result ->
                    animeNewsAdapter.submitList(result.data)
                    animeNewsProgressBar.isVisible =
                        result is Resource.Loading && result.data.isNullOrEmpty()
                    animeNewsErrorTextView.isVisible =
                        result is Resource.Error && result.data.isNullOrEmpty()
                    animeNewsErrorTextView.text =
                        result.error?.localizedMessage ?: "Aw snap an error occurred"
                }
            }
        }
    }

    override fun onItemClick(animeNews: AnimeNews) {
        val uri = Uri.parse("https://www.cbr.com/${animeNews.url}")
        val intent = Intent(Intent.ACTION_VIEW,uri)
        requireContext().startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}