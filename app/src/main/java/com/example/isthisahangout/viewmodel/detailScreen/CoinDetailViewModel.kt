package com.example.isthisahangout.viewmodel.detailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.models.cryptocoin.CoinDetail
import com.example.isthisahangout.repository.StockMarketRepository
import com.example.isthisahangout.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

const val COIN_ID = "coinId"

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stockMarketRepository: StockMarketRepository
) : ViewModel() {
    private val coinId = savedStateHandle.get<String>(COIN_ID)!!
    private val _state = MutableStateFlow(CoinDetailState())
    val state = _state.asStateFlow()

    init {
        stockMarketRepository.getCoinDetail(coinId).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = CoinDetailState(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = CoinDetailState(coin = result.data)
                }
                is Resource.Error -> {
                    _state.value = CoinDetailState(
                        error = result.error?.localizedMessage ?: ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}

data class CoinDetailState(
    val isLoading: Boolean = false,
    val coin: CoinDetail? = null,
    val error: String = ""
)