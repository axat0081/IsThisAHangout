package com.example.isthisahangout.ui.createContent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.isthisahangout.OnLoadingStateChangeListener
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentAddReminderBinding
import com.example.isthisahangout.viewmodel.AddReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.DateFormat
import java.util.*

@AndroidEntryPoint
class AddReminderFragment : Fragment(R.layout.fragment_add_reminder),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private var _binding: FragmentAddReminderBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AddReminderViewModel>()
    private lateinit var loadingStateChangeListener: OnLoadingStateChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddReminderBinding.bind(view)
        binding.apply {
            reminderTitleEditText.editText!!.setText(viewModel.reminderName.value)
            reminderDescEditText.setText(viewModel.reminderDesc.value)
            reminderTitleEditText.editText!!.addTextChangedListener {
                viewModel.setReminderName(it.toString())
            }
            reminderDescEditText.addTextChangedListener {
                viewModel.setReminderDesc(it.toString())
            }
            reminderDescEditText.setStylesBar(stylesBar)
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.reminderTime.collect { time ->
                        reminderTimeTextView.text =
                            DateFormat.getDateTimeInstance().format(time).dropLast(3)
                    }
                }
            }
            containerDueDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    requireContext(),
                    this@AddReminderFragment,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            addReminderButton.setOnClickListener {
                viewModel.onReminderAddClick()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isLoading.collectLatest { isLoading ->
                        loadingStateChangeListener.toggleLoadingState(isLoading)
                        reminderProgressBar.isVisible = isLoading
                        addReminderButton.isVisible = !isLoading
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.addReminderEventFlow.collectLatest { event ->
                        when (event) {
                            is AddReminderViewModel.AddReminderEvent.AddReminderFailure -> {
                                Toast.makeText(
                                    requireContext(),
                                    event.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is AddReminderViewModel.AddReminderEvent.AddReminderSuccess -> {
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
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.onDateSet(year = year, month = month, day = dayOfMonth)
        Log.e("time", "$year $month $dayOfMonth")
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.onTimeSet(hour = hourOfDay, minute = minute)
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