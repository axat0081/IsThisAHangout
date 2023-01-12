package com.example.isthisahangout.ui.detailsscreen

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentStockMarketCompanyDetailBinding
import com.example.isthisahangout.ui.components.StockChart
import com.example.isthisahangout.viewmodel.detailScreen.StockCompanyDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockMarketCompanyDetailFragment : Fragment(R.layout.fragment_stock_market_company_detail) {
    private var _binding: FragmentStockMarketCompanyDetailBinding? = null
    private val binding: FragmentStockMarketCompanyDetailBinding get() = _binding!!
    private val viewModel by viewModels<StockCompanyDetailViewModel>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStockMarketCompanyDetailBinding.bind(view)
        binding.apply {
            stockMarketDetailComposeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            stockMarketDetailComposeView.setContent {
                val state = viewModel.stockCompanyInfoState.collectAsState().value
                if(state.error == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF060D2E))
                            .padding(16.dp)
                    ) {
                        state.company?.let { company ->
                            Text(
                                text = company.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = company.symbol,
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Industry: ${company.industry}",
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Country: ${company.country}",
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = company.description,
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            if(state.dayInfo.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Market Summary")
                                Spacer(modifier = Modifier.height(32.dp))
                                StockChart(
                                    infoList = state.dayInfo,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .align(CenterHorizontally)
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Center
                ) {
                    if(state.isLoading) {
                        CircularProgressIndicator()
                    } else if(state.error != null) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colors.error
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}