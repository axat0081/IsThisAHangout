package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.openAI.OpenAIChatAdapter
import com.example.isthisahangout.databinding.FragmentOpenAiBinding
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.OpenAIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OpenAIFragment : Fragment(R.layout.fragment_open_ai) {
    private var _binding: FragmentOpenAiBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<OpenAIViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOpenAiBinding.bind(view)
        val openAIChatAdapter = OpenAIChatAdapter()
        binding.apply {
            openaiChatRecyclerview.apply {
                adapter = openAIChatAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.chatMessages.collectLatest { result ->
                        if (result == null) return@collectLatest
                        openAIChatAdapter.submitList(result.data)
                        when (result) {
                            is Resource.Loading -> {
                                openaiChatProgressBar.isVisible = true
                                sendMessageLayout.isVisible = false
                                openaiChatErrorTextView.isVisible = false
                            }
                            is Resource.Success -> {
                                openaiChatProgressBar.isVisible = false
                                sendMessageLayout.isVisible = true
                                openaiChatErrorTextView.isVisible = false
                            }
                            is Resource.Error -> {
                                openaiChatProgressBar.isVisible = false
                                sendMessageLayout.isVisible = false
                                openaiChatErrorTextView.isVisible = true
                            }
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.openAIChannelFlow.collect { event ->
                        when (event) {
                            is OpenAIViewModel.OpeAIChatEvent.MessageSendFailure -> {
                                Toast.makeText(
                                    requireContext(),
                                    event.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

            openAiMessageEditText.setText(viewModel.openAIChatMessage.value)

            openAiMessageEditText.addTextChangedListener {
                viewModel.setOnChatMessageChange(it.toString())
            }

            openaiChatSendButton.setOnClickListener {
                viewModel.onSendClick()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}