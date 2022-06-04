package com.example.isthisahangout.adapter

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.SongDisplayLayoutBinding
import com.example.isthisahangout.models.Song

class SongAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Song, SongAdapter.SongViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder =
        SongViewHolder(
            SongDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(song: Song)
    }

    inner class SongViewHolder(private val binding: SongDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(song: Song) {
            binding.apply {
                songTiteTextView.text = song.title
                uploadedByTextView.text = "Uploaded by: ${song.username}"
                timeTextView.text =
                    "Uploaded on - " + DateFormat.getDateTimeInstance().format(song.time)
            }
        }
    }
}