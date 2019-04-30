package com.nick.mowen.wearossms.service

import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.wearable.*
import com.nick.mowen.wearossms.extension.buildSmsNotification
import com.nick.mowen.wearossms.extension.loadBitmapFromAsset
import com.nick.mowen.wearossms.extension.saveBitmap

class MessageService : WearableListenerService() {

    override fun onMessageReceived(event: MessageEvent?) {
        Log.i("WEAR", "Received Prefs ${event?.path}")

        when (event?.path) {
            null -> {
            }
            "/sms" -> String(event.data).split("<::>").also { buildSmsNotification(it[0], it[1]).notify(it[0].hashCode()) }
            else -> {
                val data = String(event.data).split(";").toTypedArray()
                val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
                editor.putInt("primaryColor", data[2].toInt())
                editor.apply()
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer?) {
        Log.i("WEAR PROFILE", "Data change called")

        if (dataEvents == null)
            return

        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/profile") {
                val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                val profileAsset = dataMapItem.dataMap.getAsset("profileImage")
                val bitmap = loadBitmapFromAsset(profileAsset)
                bitmap.saveBitmap(this)
            }
        }
    }
}