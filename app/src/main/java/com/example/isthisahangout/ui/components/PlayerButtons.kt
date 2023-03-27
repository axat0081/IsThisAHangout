package com.example.isthisahangout.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.isthisahangout.R

@Composable
fun PlayerButtons(
    modifier: Modifier = Modifier,
    playWhenReady: Boolean,
    play: () -> Unit,
    pause: () -> Unit,
    replay10: () -> Unit,
    forward10: () -> Unit,
    next: () -> Unit,
    previous: () -> Unit,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = previous,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.song_skip_prev),
                modifier = Modifier.size(sideButtonSize),
                contentDescription = stringResource(id = R.string.previous),
                tint = Color(0xFFF92672)
            )
        }
        IconButton(
            onClick = replay10,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.song_rewind),
                modifier = Modifier.size(sideButtonSize),
                contentDescription = stringResource(id = R.string.replay10),
                tint = Color(0xFFF92672)
            )
        }
        Crossfade(targetState = playWhenReady, animationSpec = spring()) { targetPlayWhenReady ->
            if (targetPlayWhenReady) {
                IconButton(
                    onClick = pause,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.song_pause),
                        modifier = Modifier.size(playerButtonSize),
                        contentDescription = stringResource(id = R.string.play),
                        tint = Color(0xFFF92672)
                    )
                }
            } else {
                IconButton(
                    onClick = play,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.song_play),
                        modifier = Modifier.size(playerButtonSize),
                        contentDescription = stringResource(id = R.string.play),
                        tint = Color(0xFFF92672)
                    )
                }
            }
        }
        IconButton(
            onClick = forward10
        ) {
            Icon(
                painter = painterResource(id = R.drawable.song_forward),
                modifier = Modifier.size(sideButtonSize),
                contentDescription = stringResource(id = R.string.forward10),
                tint = Color(0xFFF92672)
            )
        }
        IconButton(
            onClick = next
        ) {
            Icon(
                painter = painterResource(id = R.drawable.song_skip_next),
                modifier = Modifier.size(sideButtonSize),
                contentDescription = stringResource(id = R.string.next),
                tint = Color(0xFFF92672)
            )
        }
    }
}