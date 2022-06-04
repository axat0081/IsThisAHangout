package com.example.isthisahangout.di

import com.example.isthisahangout.api.AnimeAPI
import com.example.isthisahangout.api.AnimePicsAPI
import com.example.isthisahangout.api.AnimeQuoteAPI
import com.example.isthisahangout.api.GameAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object APIModule {

    //Anime
    @Singleton
    @Provides
    @Named("AnimeAPI")
    fun providesAnimeRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(AnimeAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun providesAnimeAPI(@Named("AnimeAPI") retrofit: Retrofit):
            AnimeAPI = retrofit.create(AnimeAPI::class.java)

    //AnimeQuote
    @Provides
    @Singleton
    @Named("AnimeQuoteAPI")
    fun providesAnimeQuoteRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(AnimeQuoteAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesAnimeQuoteAPI(@Named("AnimeQuoteAPI") retrofit: Retrofit): AnimeQuoteAPI =
        retrofit.create(AnimeQuoteAPI::class.java)

    //AnimePics
    @Provides
    @Singleton
    @Named("AnimePicsAPI")
    fun providesAnimePicsRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(AnimePicsAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesAnimePicsAPI(@Named("AnimePicsAPI")retrofit: Retrofit): AnimePicsAPI =
        retrofit.create(AnimePicsAPI::class.java)

    //Games
    @Singleton
    @Provides
    @Named("GameAPI")
    fun providesGameRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(GameAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesGameAPI(@Named("GameAPI") retrofit: Retrofit): GameAPI =
        retrofit.create(GameAPI::class.java)
}