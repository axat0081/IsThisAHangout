package com.example.isthisahangout.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.C
import androidx.media3.common.Player
import com.example.isthisahangout.R
import kotlinx.coroutines.CoroutineScope
import java.util.*
import kotlin.random.Random

private const val ProgressDivider = 100f
private const val ZeroProgress = 0f

val <T> T.exhaustive: T
    get() = this

internal fun convertToProgress(count: Long, total: Long) =
    ((count * ProgressDivider) / total / ProgressDivider).takeIf(Float::isFinite) ?: ZeroProgress

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

enum class PlaybackState { IDLE, BUFFERING, READY, ENDED }

internal fun Int.asPlaybackState() = when (this) {
    Player.STATE_IDLE -> PlaybackState.IDLE
    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
    Player.STATE_READY -> PlaybackState.READY
    Player.STATE_ENDED -> PlaybackState.ENDED
    else -> PlaybackState.BUFFERING
}

val LocalSpacing = compositionLocalOf { Dimensions() }
internal fun Long.orDefaultTimestamp() = takeIf { it != C.TIME_UNSET } ?: 0L
fun convertToPosition(value: Float, total: Long) = (value * total).toLong()
fun Random.nextFloat(start: Float, end: Float) = start + nextFloat() * (end - start)

@Composable
fun Int.asFormattedString() = stringResource(
    id = R.string.player_timestamp_format,
    String.format(locale = Locale.US, format = "%02d", this / 60),
    String.format(locale = Locale.US, format = "%02d", this % 60)
)