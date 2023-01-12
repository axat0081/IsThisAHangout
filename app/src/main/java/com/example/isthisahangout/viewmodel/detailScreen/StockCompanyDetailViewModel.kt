package com.example.isthisahangout.viewmodel.detailScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.models.stockCompanies.IntradayInfo
import com.example.isthisahangout.models.stockCompanies.StockCompany
import com.example.isthisahangout.repository.StockMarketRepository
import com.example.isthisahangout.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val STOCK_COMPANY_SYMBOL = "symbol"

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class StockCompanyDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val stockMarketRepository: StockMarketRepository
) : ViewModel() {
    private val _stockCompanyInfoState = MutableStateFlow(CompanyInfoState())
    val stockCompanyInfoState = _stockCompanyInfoState.asStateFlow()

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>(STOCK_COMPANY_SYMBOL) ?: return@launch
            _stockCompanyInfoState.value = _stockCompanyInfoState.value.copy(isLoading = true)
            val stockCompanyDetailResult = async {
                stockMarketRepository.getCompanyInfo(symbol)
            }
            val intraDayInfo = async {
                stockMarketRepository.getIntraDayInfo(symbol)
            }
            when (val result = stockCompanyDetailResult.await()) {
                is Resource.Success -> {
                    _stockCompanyInfoState.value = _stockCompanyInfoState.value.copy(
                        isLoading = false,
                        company = result.data
                    )
                }
                is Resource.Error -> {
                    _stockCompanyInfoState.value = _stockCompanyInfoState.value.copy(
                        isLoading = false,
                        error = result.error?.localizedMessage ?: "Aw snap an error occurred"
                    )
                }
                else -> Unit
            }
            when (val result = intraDayInfo.await()) {
                is Resource.Success -> {
                    _stockCompanyInfoState.value = _stockCompanyInfoState.value.copy(
                        isLoading = false,
                        dayInfo = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _stockCompanyInfoState.value = _stockCompanyInfoState.value.copy(
                        isLoading = false,
                        error = result.error?.localizedMessage ?: "Aw snap an error occurred"
                    )
                }
                else -> Unit
            }
        }
    }
}

data class CompanyInfoState(
    val dayInfo: List<IntradayInfo> = emptyList(),
    val company: StockCompany? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)