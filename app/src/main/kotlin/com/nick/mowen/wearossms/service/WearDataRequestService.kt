package com.nick.mowen.wearossms.service

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.provider.Telephony
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.wearable.*
import com.klinker.android.send_message.Transaction
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.extension.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.ref.WeakReference

class WearDataRequestService : WearableListenerService(), CapabilityClient.OnCapabilityChangedListener {

    private var type = ""
    private var thread = ""
    private var uri = ""

    override fun onMessageReceived(event: MessageEvent) {
        val request = String(event.data)
        type = event.path

        when (type) {
            "/inbox" -> startClient()
            "/conversation" -> {
                thread = request
                startClient()
            }
            "/open" -> startActivity(Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", request, null)).apply { `package` = Telephony.Sms.getDefaultSmsPackage(this@WearDataRequestService) })
            "/message" -> {
                val info = request.split("SEP")
                val address = info[1]
                val message = info[2]

                if (address.contains(","))
                    sendGroupMessage(address, message, Transaction.NO_THREAD_ID)
                else
                    sendSMSMessage(address, message)
            }
            "/mms" -> {
                uri = request.trim()
                startClient()
            }
        }
    }

    private fun startClient() {
        when (type) {
            "/inbox" -> {
                val raw = if (checkStartupPermission()) getInbox() else getString(R.string.error_inbox_permissions)
                Log.i("Inbox constructed", raw)
                Wearable.getCapabilityClient(this).getCapability(Constants.TEXT_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                        .addOnSuccessListener { map ->
                            val node = map.nodes.getNode()

                            if (node == null) {
                                Log.e("Node Sending", "Node found was null")
                                return@addOnSuccessListener
                            }

                            Wearable.getMessageClient(this).sendMessage(node, "Inbox", raw.toByteArray()).apply {
                                addOnSuccessListener {
                                    Log.i("WEAR", "Sending Inbox success")
                                }
                                addOnFailureListener {
                                    Log.e("WEAR", it.toString())
                                }
                                sendMessage(node, "Inbox", raw.toByteArray())
                            }
                        }

            }
            "/conversation" -> {
                val rawConversation = if (checkStartupPermission()) getConversation(thread) else getString(R.string.error_convo_permissions)

                if (rawConversation.isNotEmpty())
                    SendConversationTask(this).execute(ServiceDataWrapper(this, rawConversation))
            }
            "/mms" -> {
                try {
                    Log.i("WEAR MMS", "Received request for $uri")
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri.toUri())
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    val image = Asset.createFromBytes(outputStream.toByteArray())
                    val dataMap = PutDataMapRequest.create("/mms")
                    dataMap.dataMap.putAsset(uri, image)
                    val request = dataMap.asPutDataRequest()
                    request.setUrgent()
                    Wearable.getDataClient(this).putDataItem(request)
                    bitmap.recycle()
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal class SendConversationTask(context: Context) : AsyncTask<ServiceDataWrapper, Void, Void>() {

        val context = WeakReference(context)

        override fun doInBackground(vararg params: ServiceDataWrapper?): Void? {
            val wrapper = params[0]
            val rawConversation = wrapper?.data

            if (wrapper != null && rawConversation != null) {
                val task = Wearable.getCapabilityClient(context.get()!!).getCapability(Constants.CONVERSATION_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                task.addOnSuccessListener { map ->
                    val node = map.nodes.getNode() ?: return@addOnSuccessListener
                    wrapper.service.sendMessage(node, "Conversation", rawConversation.toByteArray())
                }
            }

            return null
        }
    }

    internal data class ServiceDataWrapper(val service: WearDataRequestService, val data: String)
}