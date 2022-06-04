package com.example.isthisahangout.api

import com.example.isthisahangout.models.GameResults
import retrofit2.http.GET
import retrofit2.http.Query

interface GameAPI {
    companion object {
        const val BASE_URL = "https://api.rawg.io/api/"
        const val key = "6be59f7de1e243e3bbdcd5e743b0a181"
    }

    @GET("games")
    suspend fun getGames(
        @Query("key") key: String = GameAPI.key,
        @Query("page") page: String,
        @Query("page_size") pageSize: String
    ): GameResults
}