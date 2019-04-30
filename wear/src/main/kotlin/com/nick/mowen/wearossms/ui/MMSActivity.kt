package com.nick.mowen.wearossms.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.helper.NodeReadyListener
import com.nick.mowen.wearossms.util.NodeUtility
import java.lang.ref.WeakReference

class MMSActivity : AbstractWearActivity(), DataClient.OnDataChangedListener {

    private lateinit var photo: Bitmap
    private var uri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mms)

        if (intent.data != null)
            uri = intent.data.toString().trim()
    }

    override fun onStart() {
        super.onStart()
        Wearable.getDataClient(this).addListener(this)
        Wearable.getCapabilityClient(this).addListener(capabilityListener, Constants.MMS_CAPABILITY)

        if (!::photo.isInitialized) {
            val data = PreferenceManager.getDefaultSharedPreferences(this).getString("pastMMS", "")

            if (data.contains(uri))
                MMSLoadTask(this).execute(uri)
            else
                MMSRequestTask(this).execute(uri)
        }
    }

    override fun onStop() {
        Wearable.getDataClient(this).removeListener(this)
        Wearable.getCapabilityClient(this).removeListener(capabilityListener, Constants.MMS_CAPABILITY)
        super.onStop()
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.i("WEAR MMS", "DataEvent received")

        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/mms") {
                val data = PreferenceManager.getDefaultSharedPreferences(this).getString("pastMMS", "")
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("pastMMS", "$data::$uri").apply()
                val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                val profileAsset = dataMapItem.dataMap.getAsset(uri)
                MMSSetTask(this).execute(profileAsset)
            }
        }

        dataEvents.release()
    }

    private val capabilityListener = CapabilityClient.OnCapabilityChangedListener {

    }

    internal class MMSSetTask(context: Context) : AsyncTask<Asset, Void, Bitmap>() {

        private val context: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg p0: Asset?): Bitmap? {
            val asset = p0[0] ?: throw IllegalArgumentException("Asset must be non-null")
            val inputStream = Tasks.await(Wearable.getDataClient(context.get()!!).getFdForAsset(asset)).inputStream
            return BitmapFactory.decodeStream(inputStream)
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null)
                (context.get() as Activity).findViewById<SubsamplingScaleImageView>(R.id.mms_large).setImage(ImageSource.bitmap(result))
            else
                Toast.makeText(context.get(), "Could not load image", Toast.LENGTH_SHORT).show()
        }
    }

    internal class MMSLoadTask(context: Context) : AsyncTask<String, Void, Asset>() {

        private val context: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg p0: String?): Asset? {
            Log.i("WEAR MMS", "Attempting to load the mms")
            val result = Tasks.await(Wearable.getNodeClient(context.get()!!).connectedNodes)

            val pic = Uri.Builder()
                    .scheme(PutDataRequest.WEAR_URI_SCHEME)
                    .path("mms")
                    .authority(result[0].id)
                    .build()

            val buffer = Tasks.await(Wearable.getDataClient(context.get()!!).getDataItems(pic))

            for (dataItem in buffer) {
                val data = DataMapItem.fromDataItem(dataItem)

                if (data != null) {
                    buffer.release()
                    return data.dataMap.getAsset(p0[0]!!)
                }
            }

            return null
        }

        override fun onPostExecute(result: Asset?) {
            if (result != null)
                MMSSetTask(context.get()!!).execute(result)
        }
    }

    internal class MMSRequestTask(context: Context) : AsyncTask<String, Void, Void>() {

        private val context: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg list: String?): Void? {
            NodeUtility.setupNodeRequest(object : NodeReadyListener {
                override fun nodeReady(id: String?) {
                    if (id != null && list[0] != null)
                        Wearable.getMessageClient(context.get()!!).sendMessage(id, "/mms", list[0]?.toByteArray())
                                .addOnCompleteListener { task ->
                                    Log.i("MMS REQUEST", "Request status: ${task.isSuccessful}")
                                }
                }
            }, context.get()!!)
            return null
        }
    }
}