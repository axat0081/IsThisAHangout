package com.example.isthisahangout.adapter.stockMarket

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.isthisahangout.databinding.StockMarketDisplayLayoutBinding
import com.example.isthisahangout.models.stockCompanies.StockCompany

class StockMarketAdapter(private val listener: OnItemClickListener) :
    ListAdapter<StockCompany, StockMarketAdapter.StockMarketViewHolder>(
        COMPARATOR
    ) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<StockCompany>() {
            override fun areItemsTheSame(oldItem: StockCompany, newItem: StockCompany): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: StockCompany, newItem: StockCompany): Boolean =
                oldItem == newItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockMarketViewHolder =
        StockMarketViewHolder(
            StockMarketDisplayLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: StockMarketViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener {
        fun onItemClick(stockCompany: StockCompany)
    }

    inner class StockMarketViewHolder(private val binding: StockMarketDisplayLayoutBinding) :
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
        fun bind(stockCompany: StockCompany) {
            binding.apply {
                companyNameTextView.text = stockCompany.name
                symbolTextView.text = "Symbol - ${stockCompany.symbol}"
                industryTextView.text = "Industry - ${stockCompany.industry}"
            }
        }
    }
}