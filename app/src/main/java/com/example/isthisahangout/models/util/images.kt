package com.example.isthisahangout.models.util

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Images(
    val jpg: Jpg
) : Parcelable {
    @Parcelize
    data class Jpg(
        val image_url: String
    ) : Parcelable
}