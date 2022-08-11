package com.example.isthisahangout.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.SeparatorLayoutBinding

class SeparatorViewHolder(private val binding: SeparatorLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(desc: String?) {
        var text = ""
        if (desc == null) {
            text = "N\nA"
        } else {
            for (c in desc) {
                text += c
                text += '\n'
            }
        }
        binding.separatorTextView.text = text
    }
}