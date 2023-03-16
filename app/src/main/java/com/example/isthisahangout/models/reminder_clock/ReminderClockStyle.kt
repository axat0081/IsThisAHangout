package com.example.isthisahangout.models.reminder_clock

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val PI = kotlin.math.PI.toFloat()

@Stable
sealed class RemindClockStyle {
    abstract val startAngle: Float
    abstract val endAngle: Float
    abstract val minPosition: Float
    abstract val maxPosition: Float
    abstract val minAlphaThreshold: Float
    abstract val maxAlphaThreshold: Float
    abstract val minVelocity: Float
    abstract val maxVelocity: Float
    abstract val minSize: Dp
    abstract val maxSize: Dp
    abstract val density: Float

    @Stable
    object Background : RemindClockStyle() {
        override val startAngle: Float = 0f
        override val endAngle: Float = 2.0f * PI
        override val minPosition: Float = 0.05f
        override val maxPosition: Float = 0.85f
        override val minAlphaThreshold: Float = 0.2f
        override val maxAlphaThreshold: Float = 0.7f
        override val minVelocity: Float = 0.6f
        override val maxVelocity: Float = 0.7f
        override val minSize: Dp = 4.dp
        override val maxSize: Dp = 8.dp
        override val density: Float = 800 / PI
    }

    @Stable
    object MinuteHand : RemindClockStyle() {
        override val startAngle: Float = -PI / 60f
        override val endAngle: Float = PI / 60f
        override val minPosition: Float = 0.0f
        override val maxPosition: Float = 0.70f
        override val minAlphaThreshold: Float = 0.1f
        override val maxAlphaThreshold: Float = 0.6f
        override val minVelocity: Float = 0.4f
        override val maxVelocity: Float = 0.8f
        override val minSize: Dp = 4.dp
        override val maxSize: Dp = 8.dp
        override val density: Float = 3000 / PI
    }

    @Stable
    object HourHand : RemindClockStyle() {
        override val startAngle: Float = -PI / 60f
        override val endAngle: Float = PI / 60f
        override val minPosition: Float = 0.0f
        override val maxPosition: Float = 0.5f
        override val minAlphaThreshold: Float = 0.1f
        override val maxAlphaThreshold: Float = 0.4f
        override val minVelocity: Float = 0.4f
        override val maxVelocity: Float = 0.8f
        override val minSize: Dp = 4.dp
        override val maxSize: Dp = 8.dp
        override val density: Float = 3000 / PI
    }

}