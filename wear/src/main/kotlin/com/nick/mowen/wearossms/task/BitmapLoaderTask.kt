package com.nick.mowen.wearossms.task

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import androidx.collection.LruCache
import android.widget.ImageView
import com.nick.mowen.wearossms.extension.getConversationPhoto
import java.lang.ref.WeakReference

class BitmapLoaderTask(context: Context, private val memoryCache: LruCache<String, Bitmap>, image: ImageView) : AsyncTask<String, Void, Bitmap>() {

    private val weakContext: WeakReference<Context> = WeakReference(context)
    private val weakImage: WeakReference<ImageView> = WeakReference(image)

    override fun doInBackground(vararg address: String): Bitmap? =
            try {
                weakContext.get()?.getConversationPhoto(memoryCache, address[0], address[1])
            } catch (e: Exception) {
                null
            }

    override fun onPostExecute(bitmap: Bitmap?) {
        if (bitmap != null)
            weakImage.get()?.setImageBitmap(bitmap)
    }
}