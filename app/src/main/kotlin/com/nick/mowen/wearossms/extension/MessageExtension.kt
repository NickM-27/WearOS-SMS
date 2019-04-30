package com.nick.mowen.wearossms.extension

import android.content.Context
import android.telephony.SmsManager
import com.klinker.android.send_message.Message
import com.klinker.android.send_message.Settings
import com.klinker.android.send_message.Transaction

fun sendSMSMessage(address: String, message: String) {
    val manager = SmsManager.getDefault()
    manager.sendTextMessage(address, null, message, null, null)
}

fun Context.sendGroupMessage(address: String, body: String, threadId: Long) {
    val send = Settings()
    send.useSystemSending = true
    send.group = true
    val transaction = Transaction(this, send)
    val message = Message(body, address.split(",").toTypedArray())
    transaction.sendNewMessage(message, threadId)
}