package com.example.isthisahangout.api

import com.example.isthisahangout.models.AnimeDetailResponse
import com.example.isthisahangout.models.MangaDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface AnimeMangaDetailAPI {
    companion object{
        const val BASE_URL = "https://api.jikan.moe/v4/"
    }

    @GET("anime/{id}/full")
    suspend fun getAnimeDetail(
        @Path("id")id: String
    ): AnimeDetailResponse

    @GET("manga/{id}/full")
    suspend fun getMangaDetail(
        @Path("id")id: String
    ): MangaDetailResponse
}