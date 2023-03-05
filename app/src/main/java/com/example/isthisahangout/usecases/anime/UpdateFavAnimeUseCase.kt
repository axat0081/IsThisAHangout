package com.example.isthisahangout.usecases.anime

import com.example.isthisahangout.models.favourites.FavAnime
import com.example.isthisahangout.repository.AnimeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateFavAnimeUseCase @Inject constructor(
    private val animeRepository: AnimeRepository,
) {
    suspend operator fun invoke(anime: FavAnime) {
        animeRepository.updateFavAnime(anime)
    }
}