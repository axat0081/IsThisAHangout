package com.example.isthisahangout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.repository.StockMarketRepository
import com.example.isthisahangout.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockMarketViewModel @Inject constructor(
    private val stockMarketRepository: StockMarketRepository
) : ViewModel() {
    var pendingScrollToTop = false
    private val stockEvenChannel = Channel<StockEvent>()
    val stockEventFlow = stockEvenChannel.receiveAsFlow()
    private val stockMarketRefreshTriggerChannel = Channel<Refresh>()
    private val stockMarketRefreshTrigger = stockMarketRefreshTriggerChannel.receiveAsFlow()

    val stockMarket = stockMarketRefreshTrigger.flatMapLatest { refresh ->
        stockMarketRepository.getStockMarkets(
            isForcedRefresh = refresh == Refresh.FORCE,
            onFetchSuccess = {
                pendingScrollToTop = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { stockEvenChannel.send(StockEvent.StockMarketError(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onManualRefresh() {
        if (stockMarket.value !is Resource.Loading) {
            viewModelScope.launch {
                stockMarketRefreshTriggerChannel.send(Refresh.FORCE)
            }
        }
    }

    fun onStart() {
        if (stockMarket.value !is Resource.Loading) {
            viewModelScope.launch {
                stockMarketRefreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    enum class Refresh {
        FORCE, NORMAL
    }

    sealed class StockEvent {
        data class StockMarketError(val error: Throwable) : StockEvent()
    }
}