package com.example.isthisahangout.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.isthisahangout.api.AnimeAPI
import com.example.isthisahangout.api.AnimeMangaDetailAPI
import com.example.isthisahangout.remotemediator.MangaByGenreRemoteMediator
import com.example.isthisahangout.remotemediator.MangaRemoteMediator
import com.example.isthisahangout.cache.manga.MangaDatabase
import com.example.isthisahangout.models.*
import com.example.isthisahangout.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepository @Inject constructor(
    private val animeAPI: AnimeAPI,
    private val mangaDB: MangaDatabase,
    private val animeMangaDetailAPI: AnimeMangaDetailAPI
) {
    private val mangaDao = mangaDB.getMangaDao()
    private val mangaDetailDao = mangaDB.getMangaDetailDao()
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

    fun getMangaDetail(id: String): Flow<Resource<MangaDetail?>> = flow {
        emit(Resource.Loading())
        val cachedAnimeDetail = mangaDetailDao.getMangaDetail(id.toInt()).first()
        emit(Resource.Loading(cachedAnimeDetail))
        try {
            val mangaDetailDto = animeMangaDetailAPI.getMangaDetail(id).data
            mangaDetailDao.insertMangaDetail(mangaDetailDto.toMangaDetail())
            val mangaDetail = mangaDetailDao.getMangaDetail(id.toInt()).first()
            emit(Resource.Success(mangaDetail))
        } catch (exception: HttpException) {
            emit(Resource.Error(data = cachedAnimeDetail, throwable = exception))
        } catch (exception: IOException) {
            emit(Resource.Error(data = cachedAnimeDetail, throwable = exception))
        }
    }
}