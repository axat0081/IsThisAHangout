package com.example.isthisahangout.di

import android.app.Application
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object MediaModule {

    @Provides
    @ViewModelScoped
    @Named("VideoPlayer")
    fun providesVideoPlayer(
        app: Application,
    ): Player =
        ExoPlayer.Builder(app).build()

    @Provides
    @ViewModelScoped
    @Named("SongPlayer")
    fun providesSongPlayer(
        app: Application,
    ): Player =
        ExoPlayer.Builder(app).build()
            .apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .setUsage(C.USAGE_MEDIA)
                        .build(),
                    true
                )
                setHandleAudioBecomingNoisy(true)
            }
}