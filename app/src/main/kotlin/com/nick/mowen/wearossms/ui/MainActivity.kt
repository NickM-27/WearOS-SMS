package com.nick.mowen.wearossms.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.transition.Explode
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.databinding.ActivityMainBinding
import com.nick.mowen.wearossms.extension.getStartupPermission
import com.nick.mowen.wearossms.extension.setActivityColor

class MainActivity : AbstractSMSActivity() {

    override lateinit var binding: ActivityMainBinding
    private val navController: NavController by lazy { findNavController(R.id.nav_host_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.NoActionBar)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        getStartupPermission()
        bindViews()
    }

    override fun bindViews() {
        binding.toolbar.apply {
            popupTheme = if (dark) R.style.DarkPopup else R.style.LightPopup
            setSupportActionBar(this)
            setActivityColor(supportActionBar)
        }
        setupActionBarWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment -> binding.fab.isVisible = true
                else -> binding.fab.isVisible = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.REQ_PERMISSION_SMS -> if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, getString(R.string.warning_no_messages), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.settingsFragment -> {
                if (navController.currentDestination?.id != R.id.settingsFragment)
                    navController.navigate(R.id.settingsFragment)

                true
            }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.REQ_CODE_SETTINGS -> setActivityColor(supportActionBar)
            else -> {

            }
        }
    }

    fun fabClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        TransitionManager.beginDelayedTransition(binding.layout, Explode())
        binding.fabGroup.isVisible = !binding.fabGroup.isVisible
    }

    fun repliesClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("premium", false)) {
            binding.fabGroup.isVisible = false
            navController.navigate(R.id.action_mainFragment_to_repliesFragment)
        }
    }

    fun syncClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        binding.fabGroup.isVisible = false
        navController.navigate(R.id.action_mainFragment_to_demoInboxFragment)
        AlertDialog.Builder(this)
                .setTitle(R.string.title_issues)
                .setMessage(R.string.explanation_issues)
                .show()
    }
}