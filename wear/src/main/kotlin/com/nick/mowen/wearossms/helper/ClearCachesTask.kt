package com.nick.mowen.wearossms.helper

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log

import com.bumptech.glide.Glide

class ClearCachesTask(context: Context, private val clearMemory: Boolean, private val clearDisk: Boolean) : AsyncTask<Void, Void, Void>() {

    @SuppressLint("StaticFieldLeak")
    private val context = context.applicationContext

    override fun onPreExecute() {
        if (clearMemory) {
            Log.i("GLIDE", "Clearing memory cache")
            Glide.get(context).clearMemory()
            Log.i("GLIDE", "Clearing memory cache finished")
        }
    }

    override fun doInBackground(params: Array<Void>): Void? {
        if (clearDisk) {
            Log.i("GLIDE", "Clearing disk cache")
            Glide.get(context).clearDiskCache()
            Log.i("GLIDE", "Clearing disk cache finished")
        }
        return null
    }

    override fun onPostExecute(result: Void) {

    }
}
