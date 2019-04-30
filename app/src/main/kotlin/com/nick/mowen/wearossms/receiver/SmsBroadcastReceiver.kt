package com.nick.mowen.wearossms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.extension.formatPhoneNumber
import com.nick.mowen.wearossms.extension.getNode
import kotlin.concurrent.thread

class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notify", false)) {
                val bundle = intent.extras

                if (bundle != null) {
                    val result = goAsync()
                    thread {
                        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                        val address = messages[0]?.originatingAddress?.formatPhoneNumber()
                                ?: return@thread
                        val body = StringBuilder()

                        for (message in messages)
                            body.append(message.messageBody)

                        Wearable.getCapabilityClient(context).getCapability(Constants.PREF_CAPABILITY, CapabilityClient.FILTER_REACHABLE).apply {
                            addOnSuccessListener { map ->
                                val node = map.nodes.getNode() ?: return@addOnSuccessListener

                                Wearable.getMessageClient(context)
                                        .sendMessage(node, "/sms", "$address<::>$body".toByteArray())
                                        .addOnSuccessListener { Log.i("WEAR SYNC", "Sending SMS notification success") }
                            }
                        }
                        result.finish()
                    }
                }
            }
        }
    }
}