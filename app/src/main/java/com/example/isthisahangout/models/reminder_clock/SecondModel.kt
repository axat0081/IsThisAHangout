package com.example.isthisahangout.models.reminder_clock

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.isthisahangout.ui.components.SystemClock

@Stable
data class SecondModel(
    val style: Style,
    val state: State,
) {
    @Stable
    sealed class Style {
        abstract val size: Dp
        abstract val color: Color

        @Stable
        object Normal : Style() {
            override val size: Dp = 4.dp
            override val color: Color = Color.White
        }
    }

    @Stable
    data class State(
        val seconds: Long = 0,
        val millis: Long = 0,
    )

    companion object
}

fun SecondModel.next(period: Long): SecondModel =
    copy(
        state = SecondModel.State(
            seconds = (state.seconds + ((state.millis + period) / 1000)) % 60,
            millis = (state.millis + period) % 1000,
        )
    )

fun SecondModel.Companion.create(
    systemClock: SystemClock,
    style: SecondModel.Style = SecondModel.Style.Normal,
) =
    systemClock
        .currentTimeMillis()
        .let {
            SecondModel(
                style = style,
                state = SecondModel.State(
                    seconds = (it / 1000) % 60,
                    millis = (it % 1000)
                )
            )
        }