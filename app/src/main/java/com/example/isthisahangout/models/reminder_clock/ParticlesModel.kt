package com.example.isthisahangout.models.reminder_clock

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill

@Stable
data class ReminderClockState(
    val offset: Float = 0f,
    val angle: Float = 0f,
    val particleSize: Float = 0f,
    val alpha: Float = 1f,
    val velocity: Float = 0f,
    val drawStyle: DrawStyle = Fill,
) {
    companion object
}

@Stable
data class ParticlesModel(
    val angleOffset: Float = 0f,
    val style: RemindClockStyle = RemindClockStyle.Background,
    val states: List<ReminderClockState> = emptyList(),
) {
    companion object
}


