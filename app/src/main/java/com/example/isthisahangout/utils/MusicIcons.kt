package com.example.isthisahangout.utils

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.isthisahangout.R

object AppIcons {
    val Settings = Icon.ImageVectorIcon(Icons.Rounded.Settings)
    val ArrowBack = Icon.ImageVectorIcon(Icons.Rounded.ArrowBack)
    val Clear = Icon.ImageVectorIcon(Icons.Rounded.Clear)
    val Music = Icon.DrawableResourceIcon(R.drawable.ic_music)
    val Repeat = Icon.DrawableResourceIcon(R.drawable.ic_repeat)
    val RepeatOne = Icon.DrawableResourceIcon(R.drawable.ic_repeat_one)
    val Shuffle = Icon.DrawableResourceIcon(R.drawable.ic_shuffle)
    val SkipPrevious = Icon.DrawableResourceIcon(R.drawable.ic_skip_previous)
    val Play = Icon.DrawableResourceIcon(R.drawable.play)
    val Pause = Icon.DrawableResourceIcon(R.drawable.pause)
    val SkipNext = Icon.DrawableResourceIcon(R.drawable.ic_skip_next)
    val Info = Icon.ImageVectorIcon(Icons.Rounded.Info)
}

sealed interface Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon
    data class DrawableResourceIcon(@DrawableRes val resourceId: Int) : Icon
}