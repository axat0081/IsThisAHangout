package com.example.isthisahangout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.isthisahangout.databinding.AnimePicsDisplayLayoutBinding
import com.example.isthisahangout.models.AnimeImage

class AnimePicsAdapter : ListAdapter<AnimeImage, AnimePicsAdapter.AnimePicsViewHolder>(
    COMPARATOR
) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<AnimeImage>() {
            override fun areItemsTheSame(oldItem: AnimeImage, newItem: AnimeImage): Boolean =
                oldItem.image == newItem.image

            override fun areContentsTheSame(oldItem: AnimeImage, newItem: AnimeImage): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimePicsViewHolder =
        AnimePicsViewHolder(
            AnimePicsDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AnimePicsViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class AnimePicsViewHolder(private val binding: AnimePicsDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(animeImage: AnimeImage) {
            binding.apply {
                animePicImageView.load(animeImage.image)
            }
        }
    }
}