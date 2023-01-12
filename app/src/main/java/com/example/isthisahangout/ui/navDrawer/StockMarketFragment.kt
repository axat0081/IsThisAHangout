package com.example.isthisahangout.ui.navDrawer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.CoinAdapter
import com.example.isthisahangout.adapter.stockMarket.StockMarketAdapter
import com.example.isthisahangout.databinding.FragmentStockMarketBinding
import com.example.isthisahangout.models.cryptocoin.Coin
import com.example.isthisahangout.models.stockCompanies.StockCompany
import com.example.isthisahangout.ui.detailsscreen.StockMarketCompanyDetailFragmentDirections
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.StockMarketViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StockMarketFragment : Fragment(R.layout.fragment_stock_market),
    StockMarketAdapter.OnItemClickListener,
    CoinAdapter.OnItemClickListener {
    private var _binding: FragmentStockMarketBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<StockMarketViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStockMarketBinding.bind(view)
        val stockMarketAdapter = StockMarketAdapter(this)
        val coinAdapter = CoinAdapter(this)
        binding.apply {
            stockMarketRecyclerview.apply {
                adapter = stockMarketAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            coinRecyclerView.apply {
                adapter = coinAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.stockMarket.collectLatest { result ->
                        if (result == null) return@collectLatest
                        stockMarketAdapter.submitList(result.data)
                        when (result) {
                            is Resource.Success -> {
                                stockMarketSwipeRefreshLayout.isRefreshing = false
                                stockMarketErrorTextView.isVisible = false
                            }
                            is Resource.Loading -> {
                                stockMarketSwipeRefreshLayout.isRefreshing = true
                                stockMarketErrorTextView.isVisible = false
                            }
                            is Resource.Error -> {
                                stockMarketSwipeRefreshLayout.isRefreshing = true
                                stockMarketErrorTextView.isVisible = true
                                stockMarketErrorTextView.text = result.error?.localizedMessage
                                    ?: getString(R.string.aw_snap_an_error_occurred)
                            }
                        }
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.coins.collect { result ->
                        if (result == null) return@collect
                        coinAdapter.submitList(result.data)
                        when (result) {
                            is Resource.Loading -> {
                                coinsProgressBar.isVisible = true
                                coinsErrorTextView.isVisible = false
                            }
                            is Resource.Success -> {
                                coinsProgressBar.isVisible = false
                                coinsErrorTextView.isVisible = false
                            }
                            is Resource.Error -> {
                                coinsProgressBar.isVisible = false
                                coinsErrorTextView.isVisible = true
                                coinsErrorTextView.text =
                                    "Aw snap an error occurred: ${result.error?.localizedMessage ?: ""}"
                            }
                        }
                    }
                }
            }
            stockMarketSwipeRefreshLayout.setOnRefreshListener {
                viewModel.onManualRefresh()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stockEventFlow.collectLatest { event ->
                    when (event) {
                        is StockMarketViewModel.StockEvent.StockMarketError -> {
                            Snackbar.make(
                                requireView(),
                                event.error.localizedMessage
                                    ?: getString(R.string.aw_snap_an_error_occurred),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onItemClick(stockCompany: StockCompany) {
        findNavController().navigate(
            StockMarketCompanyDetailFragmentDirections.actionGlobalStockMarketCompanyDetailFragment2(
                stockCompany.symbol
            )
        )
    }

    override fun onItemClick(coin: Coin) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}