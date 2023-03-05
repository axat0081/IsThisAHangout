package com.example.isthisahangout.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.VerticalLoadStateAdapter
import com.example.isthisahangout.adapter.chat.ChatRealTimeAdapter
import com.example.isthisahangout.adapter.chat.MessagesPagingAdapter
import com.example.isthisahangout.databinding.FragmentChatBinding
import com.example.isthisahangout.utils.observeFlows
import com.example.isthisahangout.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class
ChatsFragment : Fragment(R.layout.fragment_chat) {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ChatViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatBinding.bind(view)
        val messagesPagingAdapter = MessagesPagingAdapter()
        val newMessagesAdapter = ChatRealTimeAdapter()
        val messagesAdapter = ConcatAdapter(
            newMessagesAdapter, messagesPagingAdapter.withLoadStateFooter(
                footer = VerticalLoadStateAdapter { messagesPagingAdapter.retry() }
            )
        )
        binding.apply {
            messagesRecyclerview.apply {
                itemAnimator = null
                adapter = messagesAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            }

            observeFlows { scope ->
                scope.launch {
                    viewModel.messagesPaged.collectLatest {
                        messagesPagingAdapter.submitData(it)
                    }
                }
                scope.launch {
                    viewModel.newMessages.collect {
                        newMessagesAdapter.submitList(it)
                    }
                }
                scope.launch {
                    viewModel.messageEventFlow.collectLatest { event ->
                        when (event) {
                            is ChatViewModel.MessagingEvent.MessageError -> {
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
                scope.launch {
                    messagesPagingAdapter.loadStateFlow.collect { loadState ->
                        val refresh = loadState.source.refresh
                        messagesProgressBar.isVisible =
                            loadState.source.refresh is LoadState.Loading
                        messagesErrorLayout.isVisible = loadState.source.refresh is LoadState.Error
                        if (refresh is LoadState.Error) {
                            val errorMessage = refresh.error.localizedMessage
                            messagesErrorTextView.text = "Can't load messages: $errorMessage"
                        }
                    }
                }
            }

            messagesRetryButton.setOnClickListener {
                messagesPagingAdapter.retry()
            }

            messageEditText.addTextChangedListener { text ->
                viewModel.text = text.toString()
            }
            sendButton.setOnClickListener {
                viewModel.onSendClick()
                if (messageEditText.text != null && messageEditText.text!!.isNotEmpty()) {
                    messageEditText.text!!.clear()
                }
                hideKeyboard(requireContext())
            }
        }
    }

    private fun hideKeyboard(mContext: Context) {
        val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            requireActivity().window
                .currentFocus!!.windowToken, 0
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}