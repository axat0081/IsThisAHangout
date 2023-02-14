package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.core.net.toUri
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

fun Song.toMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(id)
    .setRequestMetadata(
        MediaItem.RequestMetadata.Builder()
            .setMediaUri(url.toUri())
            .build()
    ).setMediaMetadata(
        MediaMetadata.Builder()
            .setArtworkUri(thumbnail.toUri())
            .setTitle(title)
            .setArtist(username)
            .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
            .setIsPlayable(true)
            .build()
    ).build()