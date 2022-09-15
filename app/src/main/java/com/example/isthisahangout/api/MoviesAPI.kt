package com.example.isthisahangout.api

import com.example.isthisahangout.models.movies.MoviesResult
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesAPI {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/movie/"
        const val API_KEY = "49245dcd6298eb4529694b8d964bf01a"
    }

    @GET("popular")
    suspend fun getMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int
    ): MoviesResult
}