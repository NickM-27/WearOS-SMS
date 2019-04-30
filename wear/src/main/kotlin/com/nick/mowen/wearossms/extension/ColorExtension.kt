package com.nick.mowen.wearossms.extension

import android.content.Context
import android.graphics.Color
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import com.nick.mowen.wearossms.R

/**
 * Gets the primary app color set by user
 *
 * @return Color [Int] of primary app color
 */
fun Context.getAppColor(): Int {
    val special = PreferenceManager.getDefaultSharedPreferences(this).getInt("primaryColor", -1)
    return if (special == -1) ContextCompat.getColor(this, R.color.colorPrimary) else special
}

fun Context.getMessageColor(): Int {
    return getAppColor().getLuminance()
}

fun Int.getMessageBackground(): Int {
    return getLuminance()
}

private fun Int.getLuminance(): Int {
    var r = Color.red(this)
    var g = Color.green(this)
    var b = Color.blue(this)

    r -= r / 2
    g -= g / 2
    b -= b / 2

    return Color.rgb(Math.max(0, r), Math.max(0, g), Math.max(0, b))
}

/**
 * Turns Color [Int] into a string hex equivalent
 *
 * @return hex value of Color
 */
fun Int.getHex(): String = String.format("#%06X", 0xFFFFFF and this)

/**
 * Gets Color [Int] from hex
 *
 * @return Color [Int] value
 */
fun String.getColor(): Int = Color.parseColor(this)