package com.example.isthisahangout.di

import android.app.Application
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
object VideoModule {

    @Provides
    @ViewModelScoped
    @Named("VideoPlayer")
    fun providesVideoPlayer(
        app: Application
    ): Player =
        ExoPlayer.Builder(app).build()
}