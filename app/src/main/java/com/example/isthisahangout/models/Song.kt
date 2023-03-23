package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: String,
    val title: String,
    val text: String,
    val url: String,
    val username: String,
    val pfp: String,
    val time: Long,
    val thumbnail: String,
) : Parcelable

@Parcelize
data class SongDto(
    val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val songUrl: String = "",
    val imageUrl: String = "",
) : Parcelable

fun SongDto.toSong(): Song {
    val subtitleParts = subtitle.split(',')
    return Song(
        id = mediaId,
        title = title,
        text = subtitleParts[2],
        url = songUrl,
        username = subtitleParts[0],
        pfp = subtitleParts[1],
        time = subtitleParts[3].toLong(),
        thumbnail = imageUrl
    )
}

fun Song.toSongDto(): SongDto =
    SongDto(
        mediaId = id,
        title = title,
        subtitle = "$username,$pfp,$text,$time",
        songUrl = url,
        imageUrl = thumbnail
    )

fun Song.toMediaItem(): MediaItem =
    MediaItem.Builder()
        .setMediaId(id)
        .setRequestMetadata(
            MediaItem.RequestMetadata.Builder()
                .setMediaUri(url.toUri())
                .build()
        )
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(username)
                .setIsPlayable(true)
                .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
                .setExtras(
                    bundleOf(
                        "text" to text,
                        "pfp" to pfp,
                        "time" to time,
                        "thumbnail" to thumbnail
                    )
                )
                .build()
        )
        .build()

fun MediaItem.asSong(): Song = Song(
    id = mediaId,
    title = mediaMetadata.title.toString(),
    text = mediaMetadata.extras?.getString("text") ?: "",
    url = requestMetadata.mediaUri.toString(),
    username = mediaMetadata.artist.toString(),
    pfp = mediaMetadata.extras?.getString("pfp") ?: "",
    time = mediaMetadata.extras?.getLong("time") ?: 0L,
    thumbnail = mediaMetadata.extras?.getString("thumbnail") ?: ""

)

