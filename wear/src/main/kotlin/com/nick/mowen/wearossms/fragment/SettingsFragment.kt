package com.nick.mowen.wearossms.fragment

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager

import com.nick.mowen.wearossms.R

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_wear)

        if (!PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("premium", false))
            findPreference("trueDark").isEnabled = false
    }
}