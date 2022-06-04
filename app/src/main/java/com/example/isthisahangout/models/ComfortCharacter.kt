package com.example.isthisahangout.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComfortCharacter(
    val name: String = "",
    val image: String = "",
    val from: String = "",
    val desc: String = "",
    val priority: Int = 0
) : Parcelable