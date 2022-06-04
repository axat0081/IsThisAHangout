package com.example.isthisahangout.api

import com.example.isthisahangout.models.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeAPI {
    companion object {
        const val BASE_URL = "https://api.jikan.moe/v3/"
    }

    @GET("top/anime/{id}/upcoming")
    suspend fun getUpcomingAnime(@Path("id") id: String): UpcomingAnimeResponse

    @GET("top/anime/{id}/airing")
    suspend fun getAiringAnime(@Path("id") id: String): AiringAnimeResponse

    @GET("search/anime")
    suspend fun getAnimeByGenre(
        @Query("genre") genre: String,
        @Query("page") page: String
    ): AnimeGenreResults

    @GET("season/{year}/{season}")
    suspend fun getAnimeBySeason(
        @Path("season") season: String,
        @Path("year") year: String
    ): AnimeSeasonResults

    @GET("search/anime")
    suspend fun getAnimebyName(
        @Query("q") name: String
    ): AnimeByNameResults

    @GET("schedule/{day}")
    suspend fun getAnimeByDay(
        @Path("day") day: String
    ): AnimeByDayResults

    @GET("top/manga/{id}")
    suspend fun getManga(
        @Path("id") page: String
    ): MangaResults

    @GET("search/manga")
    suspend fun getMangaByGenre(
        @Query("page") page: Int,
        @Query("genre") genre: String
    ): MangaGenreResults
}