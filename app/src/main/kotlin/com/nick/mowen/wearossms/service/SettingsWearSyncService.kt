package com.nick.mowen.wearossms.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.extension.getAppColor
import com.nick.mowen.wearossms.extension.getNode
import java.io.ByteArrayOutputStream

class SettingsWearSyncService : IntentService("WearSync") {

    /**
     * Checks settings and activates appropriate tasks
     *
     * @param intent unused
     */
    override fun onHandleIntent(intent: Intent?) {
        RequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ServiceDataWrapper(this, "Settings"))
        ProfileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ServiceDataWrapper(this, "Profile"))
    }

    /**
     *
     */
    internal class RequestTask : AsyncTask<ServiceDataWrapper, Void, Void>() {

        /**
         *
         */
        override fun doInBackground(vararg params: ServiceDataWrapper?): Void? {
            val wrapper = params[0] ?: return null
            val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(wrapper.context)

            val data = ("${defaultPrefs.getBoolean("divider", false)};${defaultPrefs.getBoolean("groupAvatar", false)};" +
                    "${wrapper.context.getAppColor()}").toByteArray()

            val task = Wearable.getCapabilityClient(wrapper.context).getCapability(Constants.PREF_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
            task.addOnSuccessListener { map ->
                val node = map.nodes.getNode() ?: return@addOnSuccessListener
                Wearable.getMessageClient(wrapper.context)
                        .sendMessage(node, "/preferences", data)
                        .addOnSuccessListener { Log.i("WEAR SYNC", "Sending prefs success") }
            }

            return null
        }
    }

    /**
     *
     */
    internal class ProfileTask : AsyncTask<ServiceDataWrapper, Void, Void>() {

        private var googleApiClient: GoogleApiClient? = null

        /**
         *
         */
        override fun doInBackground(vararg params: ServiceDataWrapper?): Void? {
            val wrapper = params[0] ?: return null
            Log.d("WearSync " + wrapper.data, "Working in background")
            val profile = PreferenceManager.getDefaultSharedPreferences(wrapper.context).getString("avatar", "")

            if (profile == "")
                return null

            try {
                Log.i("WEAR SYNC", "Sending bitmap " + profile!!)
                val bitmap = BitmapFactory.decodeFile(profile)
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
                val image = Asset.createFromBytes(outputStream.toByteArray())
                val dataMap = PutDataMapRequest.create("/profile")
                dataMap.dataMap.putAsset("profileImage", image)
                val request = dataMap.asPutDataRequest()
                request.setUrgent()
                Wearable.getDataClient(wrapper.context).putDataItem(request)
                bitmap.recycle()
                outputStream.close()
            } catch (ignored: Exception) {
                ignored.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            googleApiClient?.disconnect()
        }
    }

    /**
     *
     */
    internal data class ServiceDataWrapper(val context: Context, val data: String)
}