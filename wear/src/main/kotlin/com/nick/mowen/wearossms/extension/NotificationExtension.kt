package com.nick.mowen.wearossms.extension

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.extension.NotificationExtension.Companion.KEY_REPLY
import com.nick.mowen.wearossms.receiver.NotificationActionReceiver

fun Context.buildSmsNotification(address: String, body: String): NotificationExtension {
    val name = address.getName(this)
    val icon = getConversationPhoto(null, address, getAppColor().toString())

    val send = NotificationCompat.Action.Builder(R.drawable.ic_reply, getString(R.string.action_reply),
            NotificationActionReceiver.getReplyIntent(this, address))
            .addRemoteInput(RemoteInput.Builder(KEY_REPLY).setLabel(getString(R.string.action_reply)).build())
            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY).build()

    val main = NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Open", NotificationActionReceiver.getOpenIntent(this, address)).build()

    return NotificationExtension(this, NotificationCompat.Builder(this, NotificationExtension.sms_channel)
            .setSmallIcon(R.drawable.ic_sms_notification)
            .setContentTitle(name)
            .setContentText(body)
            .setColor(getAppColor())
            .setLargeIcon(icon)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.MessagingStyle(Person.Builder().setName("You").build())
                    .addMessage(body, System.currentTimeMillis(), Person.Builder().setName(name).setIcon(IconCompat.createWithBitmap(icon)).build()))
            .addAction(send)
            .addAction(main)
            .extend(NotificationCompat.WearableExtender().setContentAction(0))
            .build())
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.createNotificationChannel() {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.notificationChannels.size == 0) {
        val channel = NotificationChannel(NotificationExtension.sms_channel, getString(R.string.channel_sms), NotificationManager.IMPORTANCE_HIGH)
        channel.description = getString(R.string.channel_sms_desc)
        channel.enableVibration(true)
        channel.setShowBadge(false)
        notificationManager.createNotificationChannel(channel)
    }
}

class NotificationExtension(private val context: Context, private val notification: Notification) {

    fun notify(id: Int) {
        NotificationManagerCompat.from(context).notify(id, notification)
    }

    companion object {

        const val sms_channel = "wear_sms_channel"
        const val KEY_REPLY = "sms_reply"
    }
}