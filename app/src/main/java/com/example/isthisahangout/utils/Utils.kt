package com.example.isthisahangout.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope

private const val ProgressDivider = 100f
private const val ZeroProgress = 0f

val <T> T.exhaustive: T
    get() = this
internal fun convertToProgress(count: Long, total: Long) =
    ((count * ProgressDivider) / total / ProgressDivider).takeIf(Float::isFinite) ?: ZeroProgress

val LocalSpacing = compositionLocalOf { Dimensions() }
data class Dimensions(
    val default: Dp = 0.dp,
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMediumSmall: Dp = 12.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceMediumLarge: Dp = 24.dp,
    val spaceLarge: Dp = 32.dp,
    val spaceExtraLarge: Dp = 64.dp,
)

inline fun Fragment.observeFlows(crossinline observationFunction: suspend (CoroutineScope) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            observationFunction(this)
        }
    }
}
