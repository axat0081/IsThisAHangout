package com.example.isthisahangout.models.reminder_clock

import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.random.Random
import com.example.isthisahangout.utils.nextFloat

fun ReminderClockState.next(
    random: Random,
    style: RemindClockStyle,
    period: Long,
): ReminderClockState {
    val unifiedPeriod = period / 10000f
    val appliedOffset = velocity * unifiedPeriod
    val newOffset = offset + appliedOffset
    return if (newOffset < style.maxPosition) {
        copy(
            offset = newOffset,
            alpha = ReminderClockState.calcAlpha(style, offset),
        )
    } else {
        ReminderClockState.create(
            random = random,
            style = style,
            offset = ReminderClockState.calcOffset(
                random = random,
                minPosition = style.minPosition,
                maxPosition = style.minPosition + appliedOffset
            ),
        )
    }
}

fun ReminderClockState.Companion.create(
    random: Random,
    style: RemindClockStyle,
    offset: Float = calcOffset(random, style.minPosition, style.maxPosition),
): ReminderClockState =
    ReminderClockState(
        offset = offset,
        angle = calcAngle(random, style),
        particleSize = calcSize(random, style),
        velocity = calcVelocity(random, style),
        drawStyle = calcDrawStyle(random),
        alpha = calcAlpha(style, offset),
    )

private fun ReminderClockState.Companion.calcOffset(
    random: Random,
    minPosition: Float,
    maxPosition: Float,
) =
    random.nextFloat(minPosition, maxPosition)

private fun ReminderClockState.Companion.calcAngle(random: Random, style: RemindClockStyle) =
    random.nextFloat(style.startAngle, style.endAngle)

private fun ReminderClockState.Companion.calcSize(random: Random, style: RemindClockStyle) =
    random.nextFloat(style.minSize.value, style.maxSize.value)

private fun ReminderClockState.Companion.calcVelocity(
    random: Random,
    style: RemindClockStyle,
) =
    random.nextFloat(style.minVelocity, style.maxVelocity)

private fun ReminderClockState.Companion.calcDrawStyle(random: Random): DrawStyle =
    if (random.nextFloat() > 0.6f) {
        Stroke(width = 1.0f)
    } else {
        Fill
    }

private fun ReminderClockState.Companion.calcAlpha(
    style: RemindClockStyle,
    offset: Float,
): Float {
    return when {
        offset > style.maxAlphaThreshold -> {
            (1f - ((offset - style.maxAlphaThreshold) / (style.maxPosition - style.maxAlphaThreshold)))
                .coerceAtLeast(0f)
        }

        offset < style.minAlphaThreshold -> {
            (1f - ((style.minAlphaThreshold - offset) / (style.minAlphaThreshold - style.minPosition)))
                .coerceIn(0f, 1f)
        }

        else -> 1f
    }
}

fun ParticlesModel.next(
    random: Random,
    period: Long,
    angleOffset: Float = 0f,
): ParticlesModel =
    copy(
        angleOffset = angleOffset,
        states = states.map { it.next(random, style, period) }
    )

fun ParticlesModel.Companion.create(
    style: RemindClockStyle = RemindClockStyle.Background,
    numParticles: Int = calculateNumParticles(style),
    random: Random,
    angleOffset: Float = 0f,
): ParticlesModel {
    val states = mutableListOf<ReminderClockState>()
    repeat(numParticles) {
        states.add(
            ReminderClockState.create(
                random = random,
                style = style,
            )
        )
    }
    return ParticlesModel(
        style = style,
        states = states,
        angleOffset = angleOffset,
    )
}

private fun ParticlesModel.Companion.calculateNumParticles(style: RemindClockStyle): Int =
    (style.density * (style.endAngle - style.startAngle)).toInt()