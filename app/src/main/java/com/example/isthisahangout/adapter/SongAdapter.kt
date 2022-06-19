package com.example.isthisahangout.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.databinding.SongDisplayLayoutBinding
import com.example.isthisahangout.models.Song

class SongAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Song, SongAdapter.SongViewHolder>(COMPARATOR) {

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
        holder.bind(getItem(position))
    }

    interface OnItemClickListener {
        fun onItemClick(song: Song)
    }

    inner class SongViewHolder(private val binding: SongDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(song: Song) {
            binding.apply {
                songTitleTextView.text = song.title
                uploadedByTextView.text = "Uploaded by - ${song.username}"
                Glide.with(itemView)
                    .load(song.thumbnail)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.isVisible = false
                            return false
                        }
                    }).into(songImageView)
            }
        }
    }
}