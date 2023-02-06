package com.example.isthisahangout.utils

import android.content.Context
import com.example.isthisahangout.R
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.io.File

const val DEFAULT_VIDEO_MAX_CACHE_SIZE = 100 * 1024 * 1024L
const val DEFAULT_VIDEO_FILE_SIZE = 10 * 1024 * 1024L

class VideoCache(
    context: Context,
) : DataSource.Factory {

    private var defaultDataSourceFactory: DefaultDataSourceFactory
    private val simpleCache = SimpleCache(
        File(context.cacheDir, "media"),
        LeastRecentlyUsedCacheEvictor(DEFAULT_VIDEO_MAX_CACHE_SIZE),
        ExoDatabaseProvider(context)
    )
    init {
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        defaultDataSourceFactory = DefaultDataSourceFactory(
            context,
            DefaultHttpDataSourceFactory(userAgent, DefaultBandwidthMeter.Builder(context).build())
        )
    }

    override fun createDataSource(): DataSource {
        return CacheDataSource(
            simpleCache, defaultDataSourceFactory.createDataSource(),
            FileDataSource(), CacheDataSink(simpleCache, DEFAULT_VIDEO_FILE_SIZE),
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
            null
        )
    }

    fun release() {
        simpleCache.release()
    }

}