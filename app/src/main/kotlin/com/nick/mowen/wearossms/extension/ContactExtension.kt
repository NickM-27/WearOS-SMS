package com.nick.mowen.wearossms.extension

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R

fun String?.getName(context: Context): String {
    if (this == null || this == "")
        return ""

    var name: String = this

    return if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(this))
        val cursor = context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
            cursor.close()
        }
        name
    } else
        ""
}

fun String?.getColor(context: Context, preferences: SharedPreferences = context.getSharedPreferences(Constants.COLOR_PREFERENCES, Context.MODE_PRIVATE)): Int {
    val name = if (this == null || this.isEmpty())
        " "
    else
        this

    preferences.getInt(name, -1).let {
        if (it != -1)
            return it
    }

    return when (name.toLowerCase()[0]) {
        'a' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[0])
        'b' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[1])
        'c' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[2])
        'd' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[3])
        'e' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[4])
        'f' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[5])
        'g' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[6])
        'h' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[7])
        'i' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[8])
        'j' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[9])
        'k' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[10])
        'l' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[11])
        'm' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[12])
        'n' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[13])
        'o' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[14])
        'p' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[15])
        'q' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[16])
        'r' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[17])
        's' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[18])
        't' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[19])
        'u' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[20])
        'v' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[21])
        'w' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[22])
        'x' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[23])
        'y' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[24])
        'z' -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[25])
        else -> Color.parseColor(context.resources.getStringArray(R.array.default_colors)[26])
    }
}