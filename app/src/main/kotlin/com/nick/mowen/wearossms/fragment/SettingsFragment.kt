package com.nick.mowen.wearossms.fragment

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.extension.*

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private var current = 0
    private var edited = false

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_main)
        findPreference("primaryColor").onPreferenceClickListener = this
        findPreference("avatar").onPreferenceClickListener = this
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(activity).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(activity).unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        edited = true
        when (key) {
            "theme" -> when (sharedPreferences.getString("theme", "Automatic Mode")) {
                "Day Mode" -> (activity as AppCompatActivity).delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "Night Mode", "True Dark Mode" -> (activity as AppCompatActivity).delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> {
                    (activity as AppCompatActivity).delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
                    requireActivity().getLocationPermission()
                }
            }
            "primaryColor" -> toggleColorAnimation(requireActivity().getAppColor())
            "notify" -> {
                Toast.makeText(requireContext(), "You may need to reenable the SMS permission for this to work", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECEIVE_SMS), 0)
            }
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            "avatar" -> {
                AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.title_option_avatar))
                    .setPositiveButton(getString(R.string.action_select)) { dialog, _ ->
                        if (requireActivity().checkStoragePermission())
                            requireActivity().getImage(Constants.REQ_CODE_IMAGE_SELECT)
                        else
                            requireActivity().getStoragePermission()

                        dialog.dismiss()
                    }.setNegativeButton(getString(R.string.action_undo)) { dialog, _ ->
                        PreferenceManager.getDefaultSharedPreferences(activity).edit { putString("avatar", "") }
                        dialog.dismiss()
                    }.show()
                edited = true
                true
            }
            "primaryColor" -> {
                requireActivity().showColorPicker(requireActivity().getAppColor().toHex()) {
                    current = requireActivity().getAppColor()

                    when (it) {
                        "NONE" -> PreferenceManager.getDefaultSharedPreferences(activity).edit { putInt("primaryColor", -1) }
                        else -> PreferenceManager.getDefaultSharedPreferences(activity).edit { putInt("primaryColor", Color.parseColor(it)) }
                    }
                }
                true
            }
            else -> false
        }
    }

    /**
     * Animates color change when selecting conversations
     *
     * @param colorTo new color selected by the user
     */
    private fun toggleColorAnimation(colorTo: Int) {
        val window = requireActivity().window
        val actionBar = (activity as AppCompatActivity).supportActionBar
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), current, colorTo)
        colorAnimation.duration = 250 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            window.statusBarColor = color.getStatusColor()
            window.navigationBarColor = color

            actionBar?.setBackgroundDrawable(ColorDrawable(color))
        }
        colorAnimation.start()
    }
}