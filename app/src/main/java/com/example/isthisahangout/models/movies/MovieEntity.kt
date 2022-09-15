package com.example.isthisahangout.models.movies

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

private const val MOVIE_POSTER_PATH = "https://www.themoviedb.org/t/p/w220_and_h330_face"

data class MoviesResult(
    val results: List<MovieDto>
)

data class MovieDto(
    val id: Long,
    val title: String,
    @SerializedName("vote_count")
    val voteCount: Double,
    val overview: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("poster_path")
    val posterPath: String
)

data class Movie(
    val id: Long,
    val title: String,
    val voteCount: Double,
    val overview: String,
    val releaseDate: String,
    val image: String
)

@Entity(tableName = "movie")
data class MovieEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val voteCount: Double,
    val overview: String,
    val releaseDate: String,
    val image: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "movies_remote_key")
data class MoviesRemoteKey(
    @PrimaryKey
   val nextPage: Int
)

fun MovieDto.toMovieEntity(): MovieEntity =
    MovieEntity(
        id = id,
        title = title,
        voteCount = voteCount,
        overview = overview,
        releaseDate = releaseDate,
        image = MOVIE_POSTER_PATH + posterPath
    )

fun MovieEntity.toMovie(): Movie =
    Movie(
        id = id,
        title = title,
        voteCount = voteCount,
        overview = overview,
        releaseDate = releaseDate,
        image = image
    )