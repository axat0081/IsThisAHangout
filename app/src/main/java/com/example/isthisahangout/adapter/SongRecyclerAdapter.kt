package com.example.isthisahangout.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.SongDisplayLayoutBinding
import com.example.isthisahangout.models.Song
import com.example.isthisahangout.utils.newSongQuery
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SongRecyclerAdapter(private val listener: OnItemClickListener) :
    FirestoreRecyclerAdapter<Song, SongRecyclerAdapter.SongViewHolder>(options) {

    companion object {
        var options: FirestoreRecyclerOptions<Song> =
            FirestoreRecyclerOptions.Builder<Song>()
                .setQuery(
                    newSongQuery,
                    Song::class.java
                )
                .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder =
        SongViewHolder(
            SongDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SongViewHolder, position: Int, model: Song) {
        val item = getItem(position)
        holder.bind(item)
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
                    listener.onItemClick(item)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(song: Song) {
            binding.apply {
                songTiteTextView.text = song.title
                uploadedByTextView.text = "Uploaded by: ${song.username}"
                timeTextView.text =
                    "Uploaded on - " + android.icu.text.DateFormat.getDateTimeInstance()
                        .format(song.time)
            }
        }
    }
}