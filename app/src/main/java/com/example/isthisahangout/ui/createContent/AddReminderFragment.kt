package com.example.isthisahangout.ui.createContent

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.isthisahangout.OnLoadingStateChangeListener
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentAddReminderBinding
import com.example.isthisahangout.viewmodel.AddReminderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddReminderFragment : Fragment(R.layout.fragment_add_reminder) {
    private var _binding: FragmentAddReminderBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<AddReminderFragmentArgs>()
    private val viewModel by viewModels<AddReminderViewModel>()
    private lateinit var loadingStateChangeListener: OnLoadingStateChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddReminderBinding.bind(view)
        val reminder = args.reminder
        viewModel.setReminderName(reminder.name ?: "")
        viewModel.setReminderDesc(reminder.desc ?: "")
        binding.apply {
            reminderTitleEditText.setText(viewModel.reminderName.value)
            reminderDescEditText.setText(viewModel.reminderDesc.value)
            reminderTitleEditText.addTextChangedListener {
                viewModel.setReminderName(it.toString())
            }
            reminderDescEditText.addTextChangedListener {
                viewModel.setReminderDesc(it.toString())
            }
//            addReminderButton.setOnClickListener {
//                viewModel.onReminderAddClick()
//            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isLoading.collectLatest { isLoading ->
                        loadingStateChangeListener.toggleLoadingState(isLoading)
                        reminderProgressBar.isVisible = isLoading
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.addReminderEventFlow.collectLatest { event ->
                        when (event) {
                            is AddReminderViewModel.AddReminderEvent.AddReminderFailure -> {
                                Snackbar.make(
                                    requireView(),
                                    event.message,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            is AddReminderViewModel.AddReminderEvent.AddReminderSuccess -> {
                                Snackbar.make(
                                    requireView(),
                                    event.message,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadingStateChangeListener = context as OnLoadingStateChangeListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}