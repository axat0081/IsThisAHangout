package com.example.isthisahangout.adapter.reminders

import com.example.isthisahangout.models.reminders.Reminder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.RemindersDisplayLayoutBinding
import java.text.DateFormat
import java.util.*

class RemindersAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Reminder, RemindersAdapter.RemindersViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Reminder>() {
            override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean =
                oldItem.time == newItem.time

            override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindersViewHolder =
        RemindersViewHolder(
            RemindersDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RemindersViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(reminder: Reminder)
        fun onCheckBoxClickListener(reminder: Reminder, isChecked: Boolean)
    }

    inner class RemindersViewHolder(private val binding: RemindersDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            listener.onItemClick(item)
                        }
                    }
                }
                checkBoxCompleted.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            listener.onCheckBoxClickListener(item, checkBoxCompleted.isChecked)
                        }
                    }
                }
            }
        }

        fun bind(reminder: Reminder) {
            binding.apply {
                binding.apply {
                    checkBoxCompleted.isChecked = reminder.done
                    textViewName.text = reminder.name
                    textViewName.paint.isStrikeThruText = reminder.done
                    val time = DateFormat.getDateTimeInstance().format(Date(reminder.time))
                    val idx = time.lastIndexOf(':')
                    dateTextView.text = time.substring(startIndex = 0, endIndex = idx - 1)
                }
            }
        }
    }
}