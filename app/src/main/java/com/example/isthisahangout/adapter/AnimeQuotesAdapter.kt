package com.example.isthisahangout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.QuoteDisplayLayoutBinding
import com.example.isthisahangout.models.RoomAnimeQuote

class AnimeQuotesAdapter :
    ListAdapter<RoomAnimeQuote, AnimeQuotesAdapter.AnimeQuoteViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<RoomAnimeQuote>() {
            override fun areItemsTheSame(oldItem: RoomAnimeQuote, newItem: RoomAnimeQuote) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: RoomAnimeQuote, newItem: RoomAnimeQuote) =
                oldItem.quote == newItem.quote
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeQuoteViewHolder =
        AnimeQuoteViewHolder(
            QuoteDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AnimeQuoteViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class AnimeQuoteViewHolder(private val binding: QuoteDisplayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(quote: RoomAnimeQuote) {
            binding.apply {
                quoteTextView.text = quote.quote
                characterNameTextView.text = " - ${quote.character}"
                animeNameTextView.text = quote.anime
            }
        }
    }
}