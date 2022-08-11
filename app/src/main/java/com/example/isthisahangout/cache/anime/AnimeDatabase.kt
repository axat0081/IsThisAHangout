package com.example.isthisahangout.cache.anime

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.*

@Database(
    entities = [UpcomingAnimeResponse.UpcomingAnime::class, AiringAnimeResponse.AiringAnime::class,
        AiringAnimeRemoteKey::class,
        UpcomingAnimeRemoteKey::class,
        RoomAnimeByGenres::class,
        AnimeByGenresRemoteKey::class,
        AnimeSeasonResults.RoomAnimeBySeason::class,
        RoomAnimeQuote::class,
        AnimeByNameResults.AnimeByName::class,
        RoomAnimeByDay::class,
        AnimeImage::class,
        AnimeNews::class,
        AnimeDetail::class],
    version = 12
)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun getUpcomingAnimeDao(): UpcomingAnimeDao
    abstract fun getAiringAnimeDoa(): AiringAnimeDao
    abstract fun getUpcomingAnimeRemoteKeyDao(): UpcomingAnimeRemoteKeyDao
    abstract fun getAiringAnimeRemoteKeyDao(): AiringAnimeRemoteKeyDao
    abstract fun getAnimeByGenreDao(): AnimeGenreDao
    abstract fun getAnimeByGenreKeyDao(): AnimeGenreKeyDao
    abstract fun getAnimeBySeasonDao(): AnimeBySeasonDao
    abstract fun getAnimeQuoteDao(): AnimeQuoteDao
    abstract fun getAnimeByNameDao(): AnimeSearchByNameDao
    abstract fun getAnimeByDayDao(): AnimeByDayDao
    abstract fun getAnimePicsDao(): AnimePicsDao
    abstract fun getAnimeNewsDao(): AnimeNewsDao
    abstract fun getAnimeDetailDao(): AnimeDetailDao
}