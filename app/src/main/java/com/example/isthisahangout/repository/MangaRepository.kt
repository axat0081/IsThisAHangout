package com.example.isthisahangout.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.isthisahangout.api.AnimeAPI
import com.example.isthisahangout.models.MangaResults
import com.example.isthisahangout.models.RoomMangaByGenre
import com.example.isthisahangout.remotemediator.MangaByGenreRemoteMediator
import com.example.isthisahangout.remotemediator.MangaRemoteMediator
import com.example.isthisahangout.cache.manga.MangaDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepository @Inject constructor(
    private val animeAPI: AnimeAPI,
    private val mangaDB: MangaDatabase
) {
    private val mangaDao = mangaDB.getMangaDao()

    fun getManga(): Flow<PagingData<MangaResults.Manga>> =
        Pager(
            config = PagingConfig(
                pageSize = 50,
                maxSize = 200
            ),
            remoteMediator = MangaRemoteMediator(
                api = animeAPI,
                db = mangaDB
            ),
            pagingSourceFactory = { mangaDao.getManga() }
        ).flow

    fun getMangaByGenre(genre: String): Flow<PagingData<RoomMangaByGenre>> =
        Pager(
            config = PagingConfig(
                pageSize = 50,
                maxSize = 200
            ),
            remoteMediator = MangaByGenreRemoteMediator(
                genre = genre,
                animeAPI = animeAPI,
                mangaDb = mangaDB
            ),
            pagingSourceFactory = { mangaDao.getRoomMangaByGenre(genre) }
        ).flow
}