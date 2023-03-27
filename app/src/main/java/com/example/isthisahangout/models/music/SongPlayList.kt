package com.example.isthisahangout.models.music

import android.os.Parcelable
import com.example.isthisahangout.models.Song
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongPlayList(
    val songs: List<Song>
): Parcelable