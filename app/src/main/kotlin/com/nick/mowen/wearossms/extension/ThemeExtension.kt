package com.nick.mowen.wearossms.extension

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.nick.mowen.wearossms.R
import java.util.*

/**
 * Activates the activities theme to the users setting choice
 *
 * @return if current theme is dark mode
 */
fun Activity.activateTheme(): Boolean {
    return when (PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "Automatic Mode")) {
        "Day Mode" -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            false
        }
        "Night Mode" -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            true
        }
        "True Dark Mode" -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            window.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            true
        }
        else -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            val hour = cal.get(Calendar.HOUR_OF_DAY).toDouble()
            hour < 6 || hour > 19
        }
    }
}

/**
 * Conversation and bar to set activity color to specific color
 *
 * @param actionBar action bar to set color of
 * @param color     conversation color to use
 * @param force flag that forces color change
 */
fun AppCompatActivity.setActivityColor(actionBar: ActionBar?, color: Int = getAppColor(), force: Boolean = false) {
    if (color != ContextCompat.getColor(this, R.color.colorPrimary) || force) {
        actionBar?.setBackgroundDrawable(ColorDrawable(color))
        window.navigationBarColor = color
        window.statusBarColor = color.getStatusColor()
    }
}