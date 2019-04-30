package com.nick.mowen.wearossms.util

import android.content.Context
import android.database.Cursor
import androidx.core.net.toUri
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object DatabaseUtility {

    fun getGroupAddress(cursor: Cursor): String {
        var own = ""//ContactUtility.getDeviceNumber(context)

        own = if (own.isNotEmpty()) own.substring(3) else "7880045"
        val addressList = ArrayList<String>()
        val index = cursor.getColumnIndex("address")

        if (!cursor.moveToFirst())
            return "Couldn't load cursor"

        do {
            val address = cursor.getString(index)

            if (address != "" && !address.contains(own) && !address.contains("token") && !addressList.contains(address))
                addressList.add(address)

        } while (cursor.moveToNext())

        return if (addressList.size != 0) {
            val name = StringBuilder(addressList[0])

            for (i in 1 until addressList.size)
                name.append(", ").append(addressList[i])

            name.toString()
        } else
            own
    }

    fun getAllMmsParts(cursor: Cursor, context: Context): Array<String> {
        val parts = arrayOf("", "", "")

        do {
            val pId = cursor.getString(cursor.getColumnIndex("_id"))
            val type = cursor.getString(cursor.getColumnIndex("ct"))
            when (type) {
                "image/*", "image/jpeg", "image/bmp", "image/gif", "image/jpg", "image/png", "text/x-vcard", "video/3gpp", "video/wav", "video/mp4", "audio/amr", "vnd.android.dir/audio" -> {
                    parts[1] += "content://mms/part/$pId "
                    parts[2] += "$type "
                }
                "text/plain" -> {
                    val data = cursor.getString(cursor.getColumnIndex("_data"))

                    if (data != null) {
                        val temp = getMmsText(pId, context)

                        if (temp != null)
                            parts[0] = temp
                    } else {
                        val temp = cursor.getString(cursor.getColumnIndex("text"))

                        if (temp != null)
                            parts[0] += temp
                    }
                }
            }
        } while (cursor.moveToNext())

        cursor.close()
        return parts
    }

    private fun getMmsText(id: String, context: Context): String? {
        val pUri = "content://mms/part/$id".toUri()
        var stream: InputStream? = null
        val builder = StringBuilder()

        try {
            stream = context.contentResolver.openInputStream(pUri)
            if (stream != null) {
                val reader = InputStreamReader(stream, "UTF-8")
                val buff = BufferedReader(reader)
                var temp: String? = buff.readLine()
                while (temp != null) {
                    builder.append(temp)
                    temp = buff.readLine()
                }
            }
        } catch (e: IOException) {
            return null
        } finally {
            if (stream != null)
                try {
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

        }

        return builder.toString()
    }
}