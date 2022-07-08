package com.example.isthisahangout.di

import android.content.Context
import com.example.isthisahangout.api.*
import com.example.isthisahangout.service.music.MusicServiceConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
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
    fun providesAnimePicsAPI(@Named("AnimePicsAPI") retrofit: Retrofit): AnimePicsAPI =
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

    //Pokemon
    @Provides
    @Singleton
    @Named("PokemonAPI")
    fun providesPokemonRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(PokemonAPI.BASE_URl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesPokemonAPI(@Named("PokemonAPI") retrofit: Retrofit): PokemonAPI =
        retrofit.create(PokemonAPI::class.java)


    //Stock Market
    @Provides
    @Singleton
    @Named("StockMarketAPI")
    fun providesStockMarketRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(StockMarketAPI.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesStockMarketAPI(@Named("StockMarketAPI") retrofit: Retrofit): StockMarketAPI =
        retrofit.create(StockMarketAPI::class.java)

    //Music
    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context
    ) = MusicServiceConnection(context)
}