package com.example.isthisahangout.usecases.manga

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.cache.favourites.FavouritesDao
import com.example.isthisahangout.cache.hiddenContent.HiddenContentDao
import com.example.isthisahangout.repository.MangaRepository
import com.example.isthisahangout.ui.models.MangaUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMangaUseCase @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val favouritesDao: FavouritesDao,
    private val hiddenContentDao: HiddenContentDao
) {
    operator fun invoke(): Flow<PagingData<MangaUIModel>> =
        mangaRepository.getManga().map { pagingData ->
            pagingData.map { manga ->
                val isFav = favouritesDao.getManga(userId = MainActivity.userId, "").first()
                    .any { favManga ->
                        favManga.title == manga.title
                    }
                val isHidden =
                    !isFav && hiddenContentDao.getHiddenManga(MainActivity.userId).first()
                        .any { hiddenManga ->
                            hiddenManga.name == manga.title
                        }
                MangaUIModel.MangaModel(
                    id = manga.id,
                    title = manga.title,
                    imageUrl = manga.imageUrl,
                    startDate = manga.startDate,
                    isHidden = isHidden,
                    isFav = isFav
                )
            }
        }.map { paginData ->
            paginData.insertSeparators { before: MangaUIModel.MangaModel?, after: MangaUIModel.MangaModel? ->
                if (after == null) {
                    return@insertSeparators null
                }
                if (before == null) {
                    return@insertSeparators MangaUIModel.MangaSeparator(
                        desc = after.startDate ?: ""
                    )
                }
                MangaUIModel.MangaSeparator(desc = after.startDate ?: "")
            }
        }
}