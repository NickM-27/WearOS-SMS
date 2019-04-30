package com.nick.mowen.wearossms.extension

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.ImageView
import androidx.collection.LruCache
import androidx.core.net.toUri
import com.nick.mowen.wearossms.task.BitmapLoaderTask

fun ImageView.setConversationPhoto(cache: LruCache<String, Bitmap>, address: String, color: String): AsyncTask<*, *, *> {
    return BitmapLoaderTask(context, cache, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, address, color)
}

fun Context.getConversationPhoto(cache: LruCache<String, Bitmap>?, address: String, color: String): Bitmap? {
    if (address.contains(","))
        return getGroupPhoto(cache, address, color)

    if (cache != null) {
        val cached = cache.get(address)

        if (cached != null)
            return cached
    }

    var photo = openSmallPhoto(address)

    if (photo == null)
        photo = getLetter(address, color)

    cache?.put(address, photo!!)

    return photo
}

private fun Context.getGroupPhoto(cache: LruCache<String, Bitmap>?, address: String, color: String): Bitmap? {
    if (cache != null) {
        val cached = cache.get(address.createUniqueIdentifier())

        if (cached != null)
            return cached
    }

    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("groupAvatar", true)) {
        val list = address.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(getSinglePhoto(cache, list[0], color), 0f, 0f, null)
        canvas.drawBitmap(getSinglePhoto(cache, list[1], color), 50f, 50f, null)

        if (list.size > 2)
            canvas.drawBitmap(getSinglePhoto(cache, list[2], color), 0f, 50f, null)

        if (list.size > 3)
            canvas.drawBitmap(getSinglePhoto(cache, list[3], color), 50f, 0f, null)

        cache?.put(address.createUniqueIdentifier(), bitmap)

        return bitmap
    } else
        return getGroupCounter(address.length - address.replace(",", "").length + 1)
}

private fun Context.getSinglePhoto(cache: LruCache<String, Bitmap>?, address: String, color: String): Bitmap {
    if (cache != null) {
        val cached = cache.get(address + "small")

        if (cached != null)
            return cached
    }

    var photo = openScaledPhoto(address)

    if (photo == null)
        photo = getLetter(address, color)

    cache?.put(address + "small", photo!!)
    return photo ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
}

private fun Context.getLetter(address: String, color: String): Bitmap? {
    val name = address.getName(this)
    val letters = name.getLetters()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = 20f
    paint.color = Color.parseColor("#ffffff")
    paint.textAlign = Paint.Align.CENTER
    val image = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawColor(Integer.valueOf(color))
    canvas.drawText(letters, 25f, 32f, paint)
    return image.getCircleBitmap()
}

private fun getGroupCounter(count: Int): Bitmap? {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = 20f
    paint.color = Color.parseColor("#ffffff")
    paint.textAlign = Paint.Align.CENTER
    val image = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawColor(Color.parseColor("#009688"))
    canvas.drawText(count.toString(), 25f, 32f, paint)
    return image.getCircleBitmap()
}

fun Bitmap.getCircleBitmap(): Bitmap {
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val color = Color.RED
    val paint = Paint()
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawOval(rectF, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    recycle()
    return output ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
}

private fun Context.openScaledPhoto(address: String): Bitmap? {
    if (address == "")
        return null

    val contact = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address))
    val phones = contentResolver.query(contact, null, null, null, null) ?: return null

    try {
        if (phones.moveToFirst()) {
            val path = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))

            if (path != null)
                return Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(contentResolver, path.toUri()).getCircleBitmap(), 50, 50, true)
        }
    } catch (ignored: Exception) {
    } finally {
        phones.close()
    }

    return null
}

private fun Context.openSmallPhoto(address: String): Bitmap? {
    if (address == "")
        return null

    val contact = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address))
    val phones = contentResolver.query(contact, null, null, null, null) ?: return null

    try {
        if (phones.moveToFirst()) {
            val path = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))

            if (path != null)
                return MediaStore.Images.Media.getBitmap(contentResolver, path.toUri()).getSmallCircleBitmap()
        }
    } catch (ignored: Exception) {
    } finally {
        phones.close()
    }

    return null
}

private fun Bitmap.getSmallCircleBitmap(): Bitmap {
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val color = Color.RED
    val paint = Paint()
    val rect = Rect(0, 0, output.width, output.height)
    val rectF = RectF(rect)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawOval(rectF, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    recycle()
    return output
}

/**
 *
 */
fun String.getLetters(): String {
    return try {
        if (trim().isNotEmpty()) {
            if (contains(" ") && length > 2) {
                val section = split(" ")
                "${section[0][0]}${section[1][0]}"
            } else {
                val one = this[0]

                if (Character.isLetter(one)) {
                    if (length > 1 && this[1] != ' ')
                        "$one${this[1]}".toUpperCase()
                    else
                        "$one".toUpperCase()
                } else
                    "#"
            }
        } else
            "#"
    } catch (e: Exception) {
        this
    }
}