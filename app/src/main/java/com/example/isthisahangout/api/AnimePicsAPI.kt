package com.example.isthisahangout.api

import com.example.isthisahangout.models.AnimePics
import com.example.isthisahangout.models.FieldHolder
import retrofit2.http.Body
import retrofit2.http.POST

interface AnimePicsAPI {
    companion object {
        const val BASE_URL = "https://api.waifu.pics/"
    }

    @POST("many/sfw/waifu")
    suspend fun getAnimePics(@Body fieldHolder: FieldHolder): AnimePics
}