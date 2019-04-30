package com.nick.mowen.wearossms.extension

import android.preference.PreferenceManager
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import androidx.core.content.ContextCompat
import com.nick.mowen.wearossms.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorExtensionAndroidTest {

    @Test
    fun testGetAppColor() {
        val context = InstrumentationRegistry.getTargetContext()
        val setting = PreferenceManager.getDefaultSharedPreferences(context).getInt("primaryColor", -1)
        val color = context.getAppColor()

        if (setting == -1)
            require(color == ContextCompat.getColor(context, R.color.colorPrimary))
        else
            require(color == setting)
    }
}