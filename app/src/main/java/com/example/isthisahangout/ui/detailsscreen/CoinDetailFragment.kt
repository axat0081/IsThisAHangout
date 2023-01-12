package com.example.isthisahangout.ui.detailsscreen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentCoinDetailBinding
import com.example.isthisahangout.ui.components.CoinTag
import com.example.isthisahangout.ui.components.TeamListItem
import com.example.isthisahangout.viewmodel.detailScreen.CoinDetailViewModel
import com.google.accompanist.flowlayout.FlowRow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoinDetailFragment : Fragment(R.layout.fragment_coin_detail) {
    private var _binding: FragmentCoinDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CoinDetailViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCoinDetailBinding.bind(view)
        binding.apply {
            coinDetailComposeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            coinDetailComposeView.setContent {
                val state = viewModel.state.collectAsState().value
                Box(
                    modifier = Modifier
                        .background(color = Color(0xFF272822))
                        .fillMaxSize(),
                ) {
                    Log.e("coin", state.coin.toString())
                    state.coin?.let { coin ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${coin.rank}. ${coin.name} (${coin.symbol})",
                                        style = MaterialTheme.typography.h6,
                                        modifier = Modifier.weight(8f),
                                        color = Color(0xFFF92672)
                                    )
                                    Text(
                                        text = if (coin.isActive) "active" else "inactive",
                                        color = if (coin.isActive) Color.Green else Color.Red,
                                        fontStyle = FontStyle.Italic,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .align(CenterVertically)
                                            .weight(2f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    text = coin.description,
                                    style = MaterialTheme.typography.body2,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    text = "Tags",
                                    style = MaterialTheme.typography.h6,
                                    color = Color(0xFFF92672)
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                FlowRow(
                                    mainAxisSpacing = 10.dp,
                                    crossAxisSpacing = 10.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    coin.tags.forEach { tag ->
                                        CoinTag(tag = tag)
                                    }
                                }
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    text = "Team members",
                                    style = MaterialTheme.typography.h6,
                                    color = Color(0xFFF92672)
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                            items(coin.team) { teamMember ->
                                TeamListItem(
                                    teamMember = teamMember,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                )
                                Divider()
                            }
                        }
                    }
                    if (state.error.isNotBlank()) {
                        Text(
                            text = state.error,
                            color = Color(0xFFF92672),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .align(Alignment.Center)
                        )
                    }
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}