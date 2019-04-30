package com.nick.mowen.wearossms.extension

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream

fun Context.createAppFolder() {
    val file = filesDir

    if (!file.exists())
        Log.i("FileUtils", "Successfully created WearOSSMS folder: " + file.mkdirs())
}

fun Bitmap?.saveBitmap(context: Context) {
    if (this == null)
        return

    context.createAppFolder()

    try {
        val file = File(context.filesDir, "profile.png")
        Log.i("WEAR PROFILE", "Delete: " + file.delete())
        val fileOutputStream = FileOutputStream(file, true)
        compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
    } catch (ignored: Exception) {
        ignored.printStackTrace()
    }
}