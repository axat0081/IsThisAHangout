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


class VideoCache(
    private val context: Context,
    private val maxCacheSize: Long,
    private val maxFileSize: Long
) : DataSource.Factory {

    private var defaultDataSourceFactory: DefaultDataSourceFactory

    init {
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        defaultDataSourceFactory = DefaultDataSourceFactory(
            context,
            DefaultHttpDataSourceFactory(userAgent, DefaultBandwidthMeter.Builder(context).build())
        )
    }

    override fun createDataSource(): DataSource {
        val simpleCache = SimpleCache(
            File(context.cacheDir, "media"),
            LeastRecentlyUsedCacheEvictor(maxCacheSize),
            ExoDatabaseProvider(context)
        )
        return CacheDataSource(
            simpleCache, defaultDataSourceFactory.createDataSource(),
            FileDataSource(), CacheDataSink(simpleCache, maxFileSize),
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
            null
        )
    }
}