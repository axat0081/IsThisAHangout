package com.example.isthisahangout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.VerticalLoadStateAdapterBinding

class VerticalLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<VerticalLoadStateAdapter.VerticalLoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): VerticalLoadStateViewHolder =
        VerticalLoadStateViewHolder(
            VerticalLoadStateAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VerticalLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class VerticalLoadStateViewHolder(
        private val binding: VerticalLoadStateAdapterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.loadStateRetryButton.setOnClickListener {
                retry.invoke()
            }
        }

         fun bind(loadState: LoadState){
             binding.loadStateRetryButton.isVisible = loadState is LoadState.Error
             binding.loadStateProgressBar.isVisible = loadState is LoadState.Loading
             binding.loadStateErrorTextView.isVisible = loadState is LoadState.Error
         }
    }
}