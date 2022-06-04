package com.example.isthisahangout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.LoadstateFooterBinding

class GeneralLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<GeneralLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder =
        LoadStateViewHolder(
            LoadstateFooterBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent,
                false
            )
        )

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(private val binding: LoadstateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.loadStateRetryButton.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                loadStateProgressBar.isVisible = loadState is LoadState.Loading
                loadStateErrorTextView.isVisible = loadState is LoadState.Error
                loadStateRetryButton.isVisible = loadState is LoadState.Error
            }
        }
    }
}