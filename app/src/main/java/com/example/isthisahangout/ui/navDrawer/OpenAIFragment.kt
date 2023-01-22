package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentOpenAiBinding


class OpenAIFragment: Fragment(R.layout.fragment_open_ai) {
    private var _binding: FragmentOpenAiBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOpenAiBinding.bind(view)
        binding.apply {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}