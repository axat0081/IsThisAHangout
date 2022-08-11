package com.example.isthisahangout.usecases.anime

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.cache.favourites.FavouritesDao
import com.example.isthisahangout.cache.hiddenContent.HiddenContentDao
import com.example.isthisahangout.repository.AnimeRepository
import com.example.isthisahangout.ui.models.AnimeUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAnimeByGenreUseCase @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val favouritesDao: FavouritesDao,
    private val hiddenContentDao: HiddenContentDao
) {
    operator fun invoke(
        query: String
    ): Flow<PagingData<AnimeUIModel>> =
        animeRepository.getAnimeByGenres(
            query = query
        ).map { pagingData ->
            val favAnime = favouritesDao.getAnime("", MainActivity.userId).first()
            val hiddenAnime = hiddenContentDao.getHiddenAnime(MainActivity.userId).first()
            pagingData.map { animeByGenre ->
                val isFav =
                    favAnime.any { it.title == animeByGenre.title && it.userId == MainActivity.userId }
                val isHidden =
                    hiddenAnime.any { it.name == animeByGenre.title && it.userId == MainActivity.userId }
                AnimeUIModel.AnimeModel(
                    id = animeByGenre.id,
                    title = animeByGenre.title,
                    imageUrl = animeByGenre.imageUrl,
                    startDate = null,
                    isHidden = isHidden && !isFav,
                    isFav = isFav
                )
            }
        }.map { pagingData ->
            pagingData.insertSeparators { before: AnimeUIModel.AnimeModel?, after: AnimeUIModel.AnimeModel? ->
                if (after == null) {
                    return@insertSeparators null
                }
                if (before == null) {
                    return@insertSeparators AnimeUIModel.AnimeSeparator(
                        desc = after.startDate ?: ""
                    )
                }
                AnimeUIModel.AnimeSeparator(desc = after.startDate ?: "")
            }
        }
}