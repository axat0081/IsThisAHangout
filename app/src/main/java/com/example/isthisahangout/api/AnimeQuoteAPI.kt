package com.example.isthisahangout.api

import com.example.isthisahangout.models.AnimeQuote
import retrofit2.http.GET

interface AnimeQuoteAPI {

    companion object{
        const val BASE_URL = "https://animechan.vercel.app/api/"
    }
    @GET("quotes")
    suspend fun getAnimeQuotes(): List<AnimeQuote>
}