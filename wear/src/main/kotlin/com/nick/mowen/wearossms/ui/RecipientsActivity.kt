package com.nick.mowen.wearossms.ui

import android.os.Bundle
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.adapter.RecipientListAdapter
import com.nick.mowen.wearossms.extension.getConversationData

class RecipientsActivity : AbstractWearActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val recipients = findViewById<WearableRecyclerView>(R.id.recipients_list)
        recipients.isEdgeItemsCenteringEnabled = true
        recipients.layoutManager = WearableLinearLayoutManager(this)
        recipients.adapter = RecipientListAdapter(this, intent.getStringExtra("DATA").getConversationData())
    }
}