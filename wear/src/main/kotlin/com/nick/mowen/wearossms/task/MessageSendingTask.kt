package com.nick.mowen.wearossms.task

import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.wearable.Wearable
import com.nick.mowen.wearossms.data.ClientWrapper
import com.nick.mowen.wearossms.helper.NodeReadyListener
import com.nick.mowen.wearossms.util.NodeUtility

class MessageSendingTask : AsyncTask<ClientWrapper, Void, Void>() {

    override fun doInBackground(vararg p0: ClientWrapper?): Void? {
        val wrapper = p0[0] ?: return null
        val data = wrapper.threadId.toByteArray()
        NodeUtility.setupMessageRequest(object : NodeReadyListener {
            override fun nodeReady(id: String?) {
                if (id != null)
                    Wearable.getMessageClient(wrapper.context).sendMessage(id, "/message", data)
                            .addOnSuccessListener {
                                Log.i("WEAR", "Conversation: true")
                            }
            }
        }, wrapper.context)
        return null
    }
}