package com.example.isthisahangout.api

import com.example.isthisahangout.models.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeAPI {
    companion object {
        const val BASE_URL = "https://api.jikan.moe/v4/"
    }

    @GET("anime")
    suspend fun getUpcomingAnime(
        @Query("page") id: String,
        @Query("status") status: String = "upcoming",
        @Query("order_by") orderBy: String = "favorites",
        @Query("sort") sort: String = "desc"
    ): UpcomingAnimeResponse

    @GET("anime")
    suspend fun getAiringAnime(
        @Query("page") id: String,
        @Query("status") status: String = "airing",
        @Query("order_by") orderBy: String = "favorites",
        @Query("sort") sort: String = "desc"
    ): AiringAnimeResponse

    @GET("anime")
    suspend fun getAnimeByGenre(
        @Query("genre") genre: String,
        @Query("page") page: String,
        @Query("order_by") orderBy: String = "favorites",
        @Query("sort") sort: String = "desc"
    ): AnimeGenreResults

    @GET("seasons/{year}/{season}")
    suspend fun getAnimeBySeason(
        @Path("season") season: String,
        @Path("year") year: String,
        @Query("order_by") orderBy: String = "favorites",
        @Query("sort") sort: String = "desc"
    ): AnimeSeasonsResults

    @GET("search/anime")
    suspend fun getAnimeByName(
        @Query("q") name: String
    ): AnimeByNameResults

    @GET("schedule/{day}")
    suspend fun getAnimeByDay(
        @Path("day") day: String
    ): AnimeByDayResults

    @GET("manga")
    suspend fun getManga(
        @Query("page") page: String
    ): MangaResults

    @GET("manga")
    suspend fun getMangaByGenre(
        @Query("page") page: Int,
        @Query("genre") genre: String,
        @Query("order_by") orderBy: String = "favorites",
        @Query("sort") sort: String = "desc"
    ): MangaGenreResults
}