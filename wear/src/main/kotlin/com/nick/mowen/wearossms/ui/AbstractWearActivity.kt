package com.nick.mowen.wearossms.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.wearable.activity.WearableActivity

abstract class AbstractWearActivity : WearableActivity() {

    protected var dynamicColor = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("trueDark", false))
            window.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dynamicColor = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("contactColor", true)
    }
}