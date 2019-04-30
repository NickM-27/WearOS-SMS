package com.nick.mowen.wearossms.extension

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.nick.mowen.wearossms.Constants

fun Context.sendInbox(inbox: String) {
    if (inbox.isNotEmpty()) {
        val task = Wearable.getCapabilityClient(this).getCapability(Constants.TEXT_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
        task.addOnSuccessListener { map ->
            val node = map.nodes.getNode() ?: return@addOnSuccessListener
            sendMessage(node, "Inbox", inbox.toByteArray())
        }
    }
}

fun Context.sendMessage(id: String, path: String, data: ByteArray) {
    val task = Wearable.getMessageClient(this).sendMessage(id, path, data)
    task.addOnSuccessListener {
        Log.i("WEAR", "Sending $path success")
    }
    task.addOnFailureListener {
        Log.e("WEAR", it.toString())
    }
}

fun Set<Node?>.getNode(): String? {
    Log.d("Node Request", "Found $size nodes")
    return firstOrNull { it != null }?.id
}