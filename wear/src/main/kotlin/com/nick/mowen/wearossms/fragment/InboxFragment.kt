package com.nick.mowen.wearossms.fragment

import android.app.ActivityOptions
import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.adapter.InboxAdapter
import com.nick.mowen.wearossms.extension.getInboxData
import com.nick.mowen.wearossms.ui.ConversationActivity

class InboxFragment : Fragment(), MessageClient.OnMessageReceivedListener {

    private lateinit var loading: View
    private lateinit var adapter: InboxAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return setupInbox(inflater.inflate(R.layout.fragment_inbox, container, false))
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("WEAR", "Found path ${messageEvent.path}")

        if (messageEvent.path == "Inbox") {
            loading.visibility = View.GONE

            val inbox = String(messageEvent.data).let {
                if (it.contains("PREMIUM_SEP"))
                    it.split("PREMIUM_SPLIT")[1]
                else
                    it
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.INBOX_LIST, inbox).apply()
            adapter.setData(inbox.getInboxData())
        }
    }

    private fun setupInbox(layout: View): View {
        loading = layout.findViewById(R.id.inbox_loading)
        val conversation = layout.findViewById<WearableRecyclerView>(R.id.sms_conversation_list)
        conversation.isEdgeItemsCenteringEnabled = true
        adapter = InboxAdapter(activity as WearableActivity, PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.INBOX_LIST, "")!!.toString().getInboxData())
        conversation.layoutManager = WearableLinearLayoutManager(context)
        conversation.adapter = adapter
        conversation.setHasFixedSize(true)
        conversation.addOnItemTouchListener(Constants.ItemClickListener(context, conversation, object : Constants.ItemClickListener.ClickListener {

            override fun onClick(view: View?, position: Int) {
                adapter.getItem(position).run {
                    if (!incognito)
                        startActivity(Intent(context, ConversationActivity::class.java).apply {
                            putExtra("THREAD_ID", thread)
                            putExtra("ADDRESS", address)
                            putExtra("REPLIES", replies)
                        }, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
                }
            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("divider", false))
            conversation.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("trueDark", false)) {
            conversation.setBackgroundColor(Color.BLACK)
            adapter.blackTheme = true
        }

        return layout
    }

    fun updateAmbient(ambient: Boolean) {
        adapter.ambient = ambient
    }
}