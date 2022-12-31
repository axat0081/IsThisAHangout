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
class GetUpcomingAnimeUseCase @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val favouritesDao: FavouritesDao,
    private val hiddenContentDao: HiddenContentDao
) {

    operator fun invoke(): Flow<PagingData<AnimeUIModel>> =
        animeRepository.getUpcomingAnime()
            .map { pagingData ->
                val favAnime = favouritesDao.getAnime("", MainActivity.userId).first()
                val hiddenAnime = hiddenContentDao.getHiddenAnime(MainActivity.userId).first()
                pagingData.map { upcomingAnime ->
                    val isFav =
                        favAnime.any { it.title == upcomingAnime.title && it.userId == MainActivity.userId }
                    val isHidden =
                        hiddenAnime.any { it.name == upcomingAnime.title && it.userId == MainActivity.userId }
                    AnimeUIModel.AnimeModel(
                        id = upcomingAnime.id,
                        title = upcomingAnime.title,
                        imageUrl = upcomingAnime.imageUrl,
                        favorites = upcomingAnime.favorites,
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
                            favorites = after.favorites
                        )
                    }
                    AnimeUIModel.AnimeSeparator(favorites = after.favorites)
                }
            }
}