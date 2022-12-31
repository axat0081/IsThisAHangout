package com.example.isthisahangout.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.AnimeSeparatorLayoutBinding
import com.example.isthisahangout.databinding.VideoGameSeparatorLayoutBinding

class AnimeSeparatorViewHolder(private val binding: AnimeSeparatorLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(favorites: Int) {
        binding.separatorTextView.text = favorites.toString()
    }
}

class VideoGameSeparatorViewHolder(private val binding: VideoGameSeparatorLayoutBinding) :
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