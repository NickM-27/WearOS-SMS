package com.nick.mowen.wearossms.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.nick.mowen.wearossms.data.ClientWrapper
import com.nick.mowen.wearossms.extension.NotificationExtension
import com.nick.mowen.wearossms.task.MessageSendingTask
import com.nick.mowen.wearossms.ui.MainActivity

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {
            "REPLY" -> {
                MessageSendingTask().execute(ClientWrapper(context, "-1SEP" + intent.getStringExtra("ADDRESS") + "SEP" + RemoteInput.getResultsFromIntent(intent).getCharSequence(NotificationExtension.KEY_REPLY)))
            }
            "OPEN" -> context.startActivity(Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }

    companion object {

        fun getReplyIntent(context: Context, address: String): PendingIntent =
                PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), Intent(context, NotificationActionReceiver::class.java).apply {
                    action = "REPLY"
                    putExtra("ADDRESS", address)
                }, PendingIntent.FLAG_UPDATE_CURRENT)

        fun getOpenIntent(context: Context, address: String): PendingIntent =
                PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), Intent(context, NotificationActionReceiver::class.java).apply {
                    action = "OPEN"
                    putExtra("ADDRESS", address)
                }, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}