package com.example.isthisahangout.api

import com.example.isthisahangout.models.cryptocoin.CoinDetailDto
import com.example.isthisahangout.models.cryptocoin.CoinDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CryptoCoinAPI {

    companion object {
        const val BASE_URL = "https://api.coinpaprika.com/"
    }

    @GET("v1/coins")
    suspend fun getCoins(): List<CoinDto>

    @GET("v1/coins/{coinId}")
    suspend fun getCoinById(@Path("coinId") coinId: String): CoinDetailDto
}