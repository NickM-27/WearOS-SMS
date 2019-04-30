package com.nick.mowen.wearossms.ui

import android.app.Activity
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.wearable.activity.ConfirmationActivity
import android.support.wearable.input.RemoteInputIntent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableRecyclerView
import androidx.wear.widget.drawer.WearableActionDrawerView
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.adapter.ConversationAdapter
import com.nick.mowen.wearossms.data.ClientWrapper
import com.nick.mowen.wearossms.data.ConversationItem
import com.nick.mowen.wearossms.extension.getAppColor
import com.nick.mowen.wearossms.extension.getConversationData
import com.nick.mowen.wearossms.helper.NodeReadyListener
import com.nick.mowen.wearossms.task.MessageSendingTask
import com.nick.mowen.wearossms.util.NodeUtility
import java.util.*

class ConversationActivity : AbstractWearActivity(), MessageClient.OnMessageReceivedListener, View.OnClickListener {

    private lateinit var conversation: WearableRecyclerView
    private lateinit var adapter: ConversationAdapter
    private lateinit var actionDrawer: WearableActionDrawerView
    private lateinit var historyPreferences: SharedPreferences
    private lateinit var replies: Array<String>
    private var raw = ""
    private var threadId = ""
    private var address = ""
    private var color = Color.WHITE
    private var group = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAmbientEnabled()
        setContentView(R.layout.activity_conversation)
        bindViews()
        val history = bindData(intent)
        conversation.setHasFixedSize(true)
        conversation.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        adapter = ConversationAdapter(this, history)
        conversation.adapter = adapter
    }

    private fun bindViews() {
        conversation = findViewById(R.id.conversation_view)
        conversation.isEdgeItemsCenteringEnabled = true
        actionDrawer = findViewById(R.id.conversation_action)
        actionDrawer.isActivated = true
        val button = layoutInflater.inflate(R.layout.view_drawer_peek, actionDrawer, false)
        button.setOnClickListener(this)
        menuInflater.inflate(R.menu.menu_conversation, actionDrawer.menu)
        actionDrawer.setPeekContent(button)
        actionDrawer.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_send_message -> {
                    sendMessage()
                    true
                }
                R.id.action_call_contact -> {
                    NodeUtility.setupCallRequest(object : NodeReadyListener {
                        override fun nodeReady(id: String?) {
                            if (id != null)
                                Wearable.getMessageClient(this@ConversationActivity)
                                        .sendMessage(id, "/call", address.toByteArray())
                        }
                    }, this@ConversationActivity)
                    true
                }
                R.id.action_view_recipients -> {
                    startActivity(Intent(this@ConversationActivity, RecipientsActivity::class.java).apply { putExtra("DATA", raw) })
                    true
                }
                R.id.action_open_phone -> {
                    NodeUtility.setupOpenRequest(object : NodeReadyListener {
                        override fun nodeReady(id: String?) {
                            if (id != null)
                                Wearable.getMessageClient(this@ConversationActivity)
                                        .sendMessage(id, "/open", address.toByteArray())
                        }
                    }, this@ConversationActivity)

                    startActivity(Intent(this@ConversationActivity, ConfirmationActivity::class.java).apply {
                        putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION)
                    })
                    true
                }
                else -> false
            }
        }

        if (PreferenceManager.getDefaultSharedPreferences(this).getInt("primaryColor", -1) != -1) {
            val color = PreferenceManager.getDefaultSharedPreferences(this).getInt("primaryColor", -1)
            button.setBackgroundColor(color)
            actionDrawer.setBackgroundColor(color)
        }
    }

    private fun bindData(intent: Intent): ArrayList<ConversationItem> {
        replies = intent.getStringArrayExtra("REPLIES")
        historyPreferences = getSharedPreferences(Constants.HISTORY_PREFERENCES, Context.MODE_PRIVATE)
        threadId = intent.getStringExtra("THREAD_ID")
        address = intent.getStringExtra("ADDRESS")
        raw = historyPreferences.getString(threadId, "") ?: ""
        val items = if (raw.isEmpty()) ArrayList() else raw.getConversationData()
        group = address.contains(",")

        if (items.isNotEmpty() && !group && dynamicColor) {
            color = items[0].color
            actionDrawer.setBackgroundColor(color)
        } else {
            color = getAppColor()
            actionDrawer.setBackgroundColor(color)
        }

        if (group)
            actionDrawer.menu.removeItem(R.id.action_call_contact)
        else
            actionDrawer.menu.removeItem(R.id.action_view_recipients)

        return items
    }

    override fun onStart() {
        super.onStart()
        Wearable.getCapabilityClient(this).addListener(capabilityListener, Constants.CONVERSATION_CAPABILITY)
        Wearable.getCapabilityClient(this).addListener(capabilityListener, Constants.MESSAGE_CAPABILITY)
        Wearable.getMessageClient(this).addListener(this)
        ConversationLoaderTask().execute(ClientWrapper(this, threadId))
    }

    override fun onStop() {
        Wearable.getCapabilityClient(this).removeListener(capabilityListener, Constants.CONVERSATION_CAPABILITY)
        Wearable.getCapabilityClient(this).removeListener(capabilityListener, Constants.MESSAGE_CAPABILITY)
        Wearable.getMessageClient(this).removeListener(this)
        super.onStop()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            "Inbox" -> {
                val list = String(messageEvent.data)
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Constants.INBOX_LIST, list).apply()
            }
            else -> {
                raw = String(messageEvent.data)
                val parts = raw.split("GROUP_SEP")
                raw = parts[0]

                if (parts.size > 1)
                    getSharedPreferences(Constants.REPLY_PREFERENCES, Context.MODE_PRIVATE).edit().putString(threadId, parts[1].trim()).apply()

                historyPreferences.edit().putString(threadId, raw).apply()
                val messages = raw.getConversationData()

                if (messages.isNotEmpty()) {
                    adapter.messages = messages
                    conversation.scrollToPosition(0)
                    val message = messages[0]

                    if (!group && dynamicColor) {
                        color = message.color
                        actionDrawer.setBackgroundColor(color)
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) = sendMessage()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.REQ_CODE_MESSAGE -> if (resultCode == Activity.RESULT_OK) {
                val results = RemoteInput.getResultsFromIntent(data)
                val text = results.getCharSequence("MESSAGE")!!.toString()
                MessageSendingTask().execute(ClientWrapper(this, threadId + "SEP" + address + "SEP" + text))
                startActivity(Intent(this, ConfirmationActivity::class.java).apply {
                    putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION)
                    putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.status_message_sent))
                })
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        adapter.ambient = true
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        adapter.ambient = false
    }

    private fun sendMessage() {
        val input = RemoteInput.Builder("MESSAGE").setLabel(getString(R.string.action_enter_message))

        Log.e("ERROR?", "Replies: ${Arrays.toString(replies)}")

        if (replies.isNotEmpty() && replies[0] != " ")
            input.setChoices(replies)

        startActivityForResult(Intent(RemoteInputIntent.ACTION_REMOTE_INPUT).apply {
            putExtra(RemoteInputIntent.EXTRA_REMOTE_INPUTS, arrayOf(input.build()))
        }, Constants.REQ_CODE_MESSAGE)
    }

    private val capabilityListener = CapabilityClient.OnCapabilityChangedListener {

    }

    internal class ConversationLoaderTask : AsyncTask<ClientWrapper, Void, Void>() {

        override fun doInBackground(vararg p0: ClientWrapper?): Void? {
            val wrapper = p0[0] ?: return null
            NodeUtility.setupNodeRequest(object : NodeReadyListener {
                override fun nodeReady(id: String?) {
                    if (id != null)
                        Wearable.getMessageClient(wrapper.context).sendMessage(id, "/conversation", wrapper.threadId.toByteArray())
                                .addOnSuccessListener {
                                    Log.i("WEAR", "Conversation: true")
                                }
                }
            }, wrapper.context)
            return null
        }
    }
}