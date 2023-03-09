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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.chat.MessagesLoadingState
import com.example.isthisahangout.databinding.FragmentChatBinding
import com.example.isthisahangout.utils.observeFlows
import com.example.isthisahangout.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsFragment : Fragment(R.layout.fragment_chat) {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ChatViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatBinding.bind(view)
        binding.apply {
            messagesRecyclerview.apply {
                itemAnimator = null
                adapter = viewModel.chatAdapter
                layoutManager =
                    LinearLayoutManager(requireContext())
            }

            viewModel.chatAdapter.loadingState.observe(viewLifecycleOwner) { loadingState ->
                when (loadingState) {
                    is MessagesLoadingState.Empty -> {
                        messagesProgressBar.isVisible = false
                        messagesHeaderProgressBar.isVisible = false
                        emptyMessagesTextView.isVisible = true
                    }
                    is MessagesLoadingState.LoadingInitial -> {
                        messagesProgressBar.isVisible = true
                        messagesHeaderProgressBar.isVisible = false
                        emptyMessagesTextView.isVisible = false
                    }
                    is MessagesLoadingState.InitialLoaded -> {
                        messagesProgressBar.isVisible = false
                        messagesHeaderProgressBar.isVisible = false
                        emptyMessagesTextView.isVisible = false
                        messagesRecyclerview.scrollToPosition(viewModel.chatAdapter.itemCount - 1)
                    }
                    is MessagesLoadingState.LoadingMore -> {
                        messagesProgressBar.isVisible = false
                        messagesHeaderProgressBar.isVisible = true
                        emptyMessagesTextView.isVisible = false
                    }
                    is MessagesLoadingState.MoreLoaded -> {
                        messagesProgressBar.isVisible = false
                        messagesHeaderProgressBar.isVisible = false
                        emptyMessagesTextView.isVisible = false
                    }
                    is MessagesLoadingState.Finished -> Unit
                    is MessagesLoadingState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Could not load messages, check your internet connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is MessagesLoadingState.NewItem -> {
                        val item = loadingState.message
                        item?.let { message->
                            if (message.senderId == MainActivity.userId) {
                                messagesRecyclerview.smoothScrollToPosition(viewModel.chatAdapter.itemCount - 1)
                            }
                        }
                    }
                    is MessagesLoadingState.DeletedItem -> Unit // will change this later
                    null -> Unit
                }
            }

            observeFlows { coroutineScope ->
                coroutineScope.launch {
                    viewModel.messageEventFlow.collect { event ->
                        when (event) {
                            is ChatViewModel.MessagingEvent.MessagesSendError -> {
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                }
            }

            messageEditText.addTextChangedListener { text ->
                viewModel.text = text.toString()
            }
            sendButton.setOnClickListener {
                viewModel.onSendClick()
                if (messageEditText.text != null && messageEditText.text.isNotEmpty()) {
                    messageEditText.text.clear()
                }
                hideKeyboard(requireContext())
            }
        }
    }

    private fun hideKeyboard(mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            requireActivity().window.currentFocus!!.windowToken, 0
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.chatAdapter.onDestroy()
    }

}