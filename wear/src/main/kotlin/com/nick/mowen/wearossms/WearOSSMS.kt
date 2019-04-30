package com.nick.mowen.wearossms

import android.app.Application
import android.graphics.Bitmap
import androidx.collection.LruCache

class WearOSSMS : Application() {

    override fun onCreate() {
        super.onCreate()
        val cacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int = bitmap.byteCount / 1024
        }
    }

    companion object {

        lateinit var memoryCache: LruCache<String, Bitmap>
    }
}
