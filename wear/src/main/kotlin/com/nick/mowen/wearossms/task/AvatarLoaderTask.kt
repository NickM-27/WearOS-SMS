package com.nick.mowen.wearossms.task

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.AsyncTask
import androidx.collection.LruCache
import android.widget.ImageView
import com.nick.mowen.wearossms.extension.getCircleBitmap
import java.io.FileNotFoundException
import java.lang.ref.WeakReference

class AvatarLoaderTask(private val memoryCache: LruCache<String, Bitmap>, image: ImageView, private val size: Int) : AsyncTask<String, Void, Bitmap>() {

    private val weakImage: WeakReference<ImageView> = WeakReference(image)

    override fun doInBackground(vararg address: String): Bitmap? =
            try {
                ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(address[0]), size, size, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)?.getCircleBitmap()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }

    override fun onPostExecute(bitmap: Bitmap?) {
        if (bitmap != null) {
            if (size == 50)
                memoryCache.put("avatar", bitmap)

            weakImage.get()?.setImageBitmap(bitmap)
        }
    }
}