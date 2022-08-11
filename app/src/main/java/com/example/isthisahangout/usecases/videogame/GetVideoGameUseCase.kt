package com.example.isthisahangout.usecases.videogame

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.isthisahangout.cache.favourites.FavouritesDao
import com.example.isthisahangout.cache.hiddenContent.HiddenContentDao
import com.example.isthisahangout.repository.GameRepository
import com.example.isthisahangout.ui.models.VideoGameUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetVideoGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val favouritesDao: FavouritesDao,
    private val hiddenContentDao: HiddenContentDao
) {
    operator fun invoke(): Flow<PagingData<VideoGameUIModel>> =
        gameRepository.getGames().map { pagingData ->
            pagingData.map { game ->
                VideoGameUIModel.VideoGameModel(
                    id = game.id,
                    name = game.name,
                    imageUrl = game.imageUrl,
                    rating = game.rating,
                    genres = game.genres,
                    screenshots = game.screenshots
                )
            }
        }.map { pagingData ->
            pagingData.insertSeparators { before: VideoGameUIModel.VideoGameModel?, after: VideoGameUIModel.VideoGameModel? ->
                if (after == null) {
                    return@insertSeparators null
                }
                if (before == null) {
                    return@insertSeparators VideoGameUIModel.VideoGameSeparator(
                        desc = after.rating ?: ""
                    )
                }
                VideoGameUIModel.VideoGameSeparator(desc = after.rating ?: "")
            }
        }
}