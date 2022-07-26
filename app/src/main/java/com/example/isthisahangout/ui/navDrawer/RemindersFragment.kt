package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.reminders.RemindersAdapter
import com.example.isthisahangout.databinding.FragmentRemindersBinding
import com.example.isthisahangout.models.reminders.Reminder
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.RemindersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RemindersFragment : Fragment(R.layout.fragment_reminders),
    RemindersAdapter.OnItemClickListener {
    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RemindersViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRemindersBinding.bind(view)
        val remindersAdapter = RemindersAdapter(this)
        binding.apply {
            remindersRecyclerview.apply {
                adapter = remindersAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            addReminderButton.setOnClickListener {
                findNavController().navigate(RemindersFragmentDirections.actionRemindersFragment2ToAddReminderFragment(Reminder()))
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.reminders.collectLatest { result ->
                        if (result == null) return@collectLatest
                        remindersAdapter.submitList(result.data)
                        remindersProgressBar.isVisible = false
                        if (result is Resource.Error) {
                            remindersErrorTextView.isVisible = true
                            remindersErrorTextView.text = result.error?.localizedMessage
                                ?: getString(R.string.aw_snap_an_error_occurred)
                        } else {
                            remindersErrorTextView.isVisible = false
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(reminder: Reminder) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}