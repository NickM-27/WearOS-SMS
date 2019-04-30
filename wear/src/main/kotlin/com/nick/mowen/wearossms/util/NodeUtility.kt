package com.nick.mowen.wearossms.util

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.helper.NodeReadyListener

object NodeUtility {

    fun setupNodeRequest(listener: NodeReadyListener, client: Context) {
        Wearable.getCapabilityClient(client).getCapability(Constants.MESSAGE_CAPABILITY, CapabilityClient.FILTER_REACHABLE).addOnSuccessListener { map ->
            listener.nodeReady(getNode(map.nodes))
        }
    }

    fun setupMessageRequest(listener: NodeReadyListener, client: Context) {
        Wearable.getCapabilityClient(client).getCapability(Constants.SENDING_CAPABILITY, CapabilityClient.FILTER_REACHABLE).addOnSuccessListener { map ->
            listener.nodeReady(getNode(map.nodes))
        }
    }

    fun setupOpenRequest(listener: NodeReadyListener, client: Context) {
        Wearable.getCapabilityClient(client).getCapability(Constants.OPENING_CAPABILITY, CapabilityClient.FILTER_REACHABLE).addOnSuccessListener { map ->
            listener.nodeReady(getNode(map.nodes))
        }
    }

    fun setupCallRequest(listener: NodeReadyListener, client: Context) {
        Wearable.getCapabilityClient(client).getCapability(Constants.CALLING_CAPABILITY, CapabilityClient.FILTER_REACHABLE).addOnSuccessListener { map ->
            listener.nodeReady(getNode(map.nodes))
        }
    }

    private fun getNode(nodes: Set<Node>): String? {
        var nodeId: String? = null

        Log.d("NODE INFO", "Found ${nodes.size} nodes")

        for (node in nodes)
            nodeId = node.id

        return nodeId
    }
}
