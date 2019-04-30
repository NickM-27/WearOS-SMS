package com.nick.mowen.wearossms.ui

import android.app.Activity
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.wearable.activity.ConfirmationActivity
import android.support.wearable.input.RemoteInputIntent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.wear.widget.drawer.WearableActionDrawerView
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.adapter.InboxDrawerAdapter
import com.nick.mowen.wearossms.data.ClientWrapper
import com.nick.mowen.wearossms.extension.createNotificationChannel
import com.nick.mowen.wearossms.extension.getAppColor
import com.nick.mowen.wearossms.extension.getContactPermission
import com.nick.mowen.wearossms.fragment.InboxFragment
import com.nick.mowen.wearossms.fragment.SettingsFragment
import com.nick.mowen.wearossms.helper.ClearCachesTask
import com.nick.mowen.wearossms.helper.NodeReadyListener
import com.nick.mowen.wearossms.task.MessageSendingTask
import com.nick.mowen.wearossms.util.NodeUtility


class MainActivity : AbstractWearActivity(), View.OnClickListener {

    private lateinit var navigationDrawer: WearableNavigationDrawerView
    private lateinit var actionDrawer: WearableActionDrawerView
    private lateinit var inboxFragment: InboxFragment
    private lateinit var settingsFragment: SettingsFragment
    private var address = ""
    private var loaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAmbientEnabled()
        setContentView(R.layout.activity_main)
        bindViews()
        navigationDrawer.setAdapter(InboxDrawerAdapter(this))
        navigationDrawer.addOnItemSelectedListener { pos ->
            when (pos) {
                0 -> fragmentManager.beginTransaction().replace(R.id.inbox_container, inboxFragment).commit()
                1 -> fragmentManager.beginTransaction().replace(R.id.inbox_container, settingsFragment).commit()
            }
        }

        inboxFragment = InboxFragment()
        settingsFragment = SettingsFragment()
        getContactPermission()
        fragmentManager.beginTransaction().replace(R.id.inbox_container, inboxFragment).commit()

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("profileRefresh", false)) {
            ClearCachesTask(this, clearMemory = true, clearDisk = true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("profileRefresh", false).apply()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel()
    }

    private fun bindViews() {
        navigationDrawer = findViewById(R.id.inbox_navigation_drawer)
        navigationDrawer.setBackgroundColor(getAppColor())
        actionDrawer = findViewById(R.id.inbox_action)
        actionDrawer.isActivated = true
        val button = layoutInflater.inflate(R.layout.view_drawer_peek, actionDrawer, false)
        button.findViewById<ImageView>(R.id.row_action).setImageResource(R.drawable.ic_send)
        button.setOnClickListener(this)
        actionDrawer.setPeekContent(button)
        menuInflater.inflate(R.menu.menu_inbox, actionDrawer.menu)
        actionDrawer.setOnMenuItemClickListener {
            if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("premium", false)) {
                Toast.makeText(this, R.string.warning_requires_premium, Toast.LENGTH_LONG).show()
                return@setOnMenuItemClickListener true
            }

            when (it?.itemId) {
                R.id.action_create_conversation -> {
                    button.performClick()
                    actionDrawer.controller.closeDrawer()
                    true
                }
                R.id.action_refresh -> {
                    InboxLoaderTask().execute(this)
                    actionDrawer.controller.closeDrawer()
                    true
                }
                else -> false
            }
        }

        if (PreferenceManager.getDefaultSharedPreferences(this).getInt("primaryColor", -1) != -1) {
            val color = PreferenceManager.getDefaultSharedPreferences(this).getInt("primaryColor", -1)
            button.setBackgroundColor(color)
            actionDrawer.setBackgroundColor(color)
            navigationDrawer.setBackgroundColor(color)
        }
    }

    override fun onStart() {
        super.onStart()
        Wearable.getCapabilityClient(this).addListener(capabilityListener, Constants.MESSAGE_CAPABILITY)
        Wearable.getMessageClient(this).addListener(inboxFragment)

        if (!loaded) {
            InboxLoaderTask().execute(this)
            loaded = true
        }
    }

    override fun onStop() {
        Wearable.getCapabilityClient(this).removeListener(capabilityListener, Constants.MESSAGE_CAPABILITY)
        Wearable.getMessageClient(this).removeListener(inboxFragment)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.REQ_CODE_CONTACT -> if (resultCode == Activity.RESULT_OK && data != null) {
                address = data.getStringExtra("ADDRESS")

                if (address != "-1") {
                    val input = RemoteInput.Builder("MESSAGE").setLabel(getString(R.string.action_enter_message))
                    startActivityForResult(Intent(RemoteInputIntent.ACTION_REMOTE_INPUT).apply { putExtra(RemoteInputIntent.EXTRA_REMOTE_INPUTS, arrayOf(input.build())) }, Constants.REQ_CODE_MESSAGE)
                }
            }
            Constants.REQ_CODE_MESSAGE -> if (resultCode == Activity.RESULT_OK && data != null) {
                val results = RemoteInput.getResultsFromIntent(data)
                val text = results.getCharSequence("MESSAGE")!!.toString()
                MessageSendingTask().execute(ClientWrapper(this, "-1SEP" + address + "SEP" + text))
                startActivity(Intent(this, ConfirmationActivity::class.java).apply {
                    putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION)
                    putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.status_message_sent))
                })
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(view: View?) {
        if (!premium) {
            getPremium()
            return
        }

        val intent = Intent(this, ContactPickerActivity::class.java)
        startActivityForResult(intent, Constants.REQ_CODE_CONTACT)
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        inboxFragment.updateAmbient(true)
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        inboxFragment.updateAmbient(false)
    }

    private val capabilityListener = CapabilityClient.OnCapabilityChangedListener {

    }

    internal class InboxLoaderTask : AsyncTask<Context, Void, Void>() {

        override fun doInBackground(vararg p0: Context?): Void? {
            val context = p0[0] ?: return null
            NodeUtility.setupNodeRequest(object : NodeReadyListener {
                override fun nodeReady(id: String?) {
                    if (id != null)
                        Wearable.getMessageClient(context).sendMessage(id, "/inbox", "Inbox".toByteArray())
                                .addOnCompleteListener { task ->
                                    Log.i("WEAR", "success: ${task.isSuccessful}")
                                }
                }
            }, context)
            return null
        }
    }
}