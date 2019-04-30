package com.nick.mowen.wearossms.extension

import android.content.Context
import android.database.Cursor
import android.preference.PreferenceManager
import android.provider.Telephony
import android.text.TextUtils
import android.util.Log
import androidx.core.net.toUri
import com.google.android.mms.pdu_alt.EncodedStringValue
import com.google.android.mms.pdu_alt.PduHeaders
import com.google.android.mms.pdu_alt.PduPersister
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.util.DatabaseUtility
import java.text.MessageFormat

fun Context.getInbox(): String {

    val replyPreferences = getSharedPreferences(Constants.REPLY_PREFERENCES, Context.MODE_PRIVATE)

    fun Cursor.getConversationData(context: Context, threadId: String, address: String, name: String): String {
        return StringBuilder().append(name).append("ITEM_SEP").append(address).append("ITEM_SEP").append(getString(getColumnIndex("body"))).append("ITEM_SEP")
                .append(name.getColor(context)).append("ITEM_SEP").append(threadId).append("ITEM_SEP").append(false).append("ITEM_SEP")
                .append(replyPreferences.getStringSet(threadId, null)?.joinToString(",") ?: " ").toString()
    }

    fun Cursor.getGroupConversationData(context: Context, threadId: String, address: String): String {
        return StringBuilder().append(address.getGroupNames(context)).append("ITEM_SEP").append(address).append("ITEM_SEP").append(getString(getColumnIndex("_id")).getMessageBody(context)).append("ITEM_SEP")
                .append(context.getAppColor()).append("ITEM_SEP").append(threadId).append("ITEM_SEP").append(false).append("ITEM_SEP")
                .append(replyPreferences.getStringSet(threadId, null)?.joinToString(",") ?: " ").toString()
    }

    val cursor: Cursor?

    try {
        cursor = contentResolver.query("content://mms-sms/conversations/".toUri(), arrayOf("*"), null, null, "normalized_date desc")
    } catch (e: Exception) {
        e.printStackTrace()
        return getInboxBackup()
    }

    if (cursor == null || !cursor.moveToFirst())
        return getInboxBackup()

    val aIndex = cursor.getColumnIndex("address")
    val tIndex = cursor.getColumnIndex("thread_id")

    val builder = StringBuilder()
    do {
        if (builder.isNotEmpty())
            builder.append("MESSAGE_SEP")

        val thread = cursor.getString(tIndex)

        if (thread != null && thread != "-1") {
            val address = cursor.getString(aIndex)

            builder.append(if (address != null)
                cursor.getConversationData(this, thread, address.formatPhoneNumber(), address.getName(this))
            else
                cursor.getGroupConversationData(this, thread, getGroupAddress(cursor.getString(cursor.getColumnIndex("_id"))).formatPhoneNumber()))
        }
    } while (cursor.moveToNext())

    cursor.close()
    return builder.toString()
}

fun Context.getInboxBackup(): String {

    val replyPreferences = getSharedPreferences(Constants.REPLY_PREFERENCES, Context.MODE_PRIVATE)

    fun getConversationData(threadId: String): String {
        val cursor = contentResolver.query("content://mms-sms/conversations/$threadId".toUri(), arrayOf("address", "body", "date"), null, null, "date DESC")

        if (cursor == null || !cursor.moveToFirst())
            return ""

        val address = cursor.getString(cursor.getColumnIndex("address")) ?: "Null"

        return if (address != "Null") {
            val name = address.getName(this)
            val conversation = StringBuilder().append(name).append("ITEM_SEP").append(address).append("ITEM_SEP").append(cursor.getString(cursor.getColumnIndex("body"))
                    ?: "").append("ITEM_SEP")
                    .append(name.getColor(this)).append("ITEM_SEP").append(threadId).append("ITEM_SEP").append(false).append("ITEM_SEP")
                    .append(replyPreferences.getStringSet(threadId, null)?.joinToString(",") ?: " ").toString()
            cursor.close()
            conversation
        } else
            ""
    }

    fun getGroupConversationData(threadId: String): String {
        val cursor = contentResolver.query("content://mms-sms/conversations/$threadId".toUri(), arrayOf("_id", "date"), null, null, "date DESC")

        if (cursor == null || !cursor.moveToFirst())
            return ""

        val id = cursor.getString(cursor.getColumnIndex("_id")) ?: return ""
        val address = getGroupAddress(id)
        val conversation = StringBuilder().append(address.getGroupNames(this)).append("ITEM_SEP").append(address).append("ITEM_SEP").append(id.getMessageBody(this)).append("ITEM_SEP")
                .append(getAppColor()).append("ITEM_SEP").append(threadId).append("ITEM_SEP").append(false).append("ITEM_SEP")
                .append(replyPreferences.getStringSet(threadId, null)?.joinToString(",") ?: " ").toString()
        cursor.close()
        return conversation
    }

    val cursor = contentResolver.query("content://mms-sms/conversations?simple=true".toUri(), null, null, null, "date DESC")
    val builder = StringBuilder()

    if (cursor == null || !cursor.moveToFirst())
        return ""

    val tIndex = cursor.getColumnIndex("_id")

    do {
        if (builder.isNotEmpty())
            builder.append("MESSAGE_SEP")

        val threadId = cursor.getString(tIndex)

        if (threadId != null) {
            builder.append(if (!cursor.getString(cursor.getColumnIndex("recipient_ids")).contains(" "))
                getConversationData(threadId) else getGroupConversationData(threadId))
        }
    } while (cursor.moveToNext())
    cursor.close()

    return builder.toString()
}

fun Context.getConversation(threadId: String, count: Int = PreferenceManager.getDefaultSharedPreferences(this).getString("conversationCount", "30")?.toInt()
        ?: 30): String {

    val cursor = contentResolver.query("content://mms-sms/conversations/$threadId".toUri(), arrayOf("address", "body", "date", "type", "m_type", "_id"), null, null, "date desc")

    if (cursor == null || !cursor.moveToFirst())
        return ""

    val aIndex = cursor.getColumnIndex("address")
    var address: String?

    if (aIndex >= 0)
        do {
            address = (cursor.getString(aIndex) ?: "")
        } while (address?.isEmpty() != false && cursor.moveToNext())
    else
        address = ""

    cursor.moveToFirst()

    var index = 0
    val list = ArrayList<Message>()
    do {
        list.add(cursor.getMessageData(this, address!!))
        index += 1
    } while (cursor.moveToNext() && index <= count)

    if (cursor.count >= count && !cursor.isAfterLast) {
        val lastDate = cursor.getLong(cursor.getColumnIndex("date"))
        cursor.moveToFirst()
        val firstDate = cursor.getLong(cursor.getColumnIndex("date"))
        cursor.moveToLast()
        var date = cursor.getLong(cursor.getColumnIndex("date"))

        Log.i("Date check", "${date * 1000} > $lastDate")
        if (date * 1000 > lastDate) {
            do {
                date = cursor.getLong(cursor.getColumnIndex("date"))
                val item = cursor.getMessageData(this, address, date)

                if (item.mms.isNotEmpty())
                    list.add(item)

                val last = cursor.moveToPrevious()
                val check = if (!last) false else cursor.getLong(cursor.getColumnIndex("date")) * 1000 > lastDate && cursor.getLong(cursor.getColumnIndex("date")) < firstDate
            } while (last && check)
        }
    }

    cursor.close()
    list.sort()
    val builder = StringBuilder()

    for (message in list.distinctBy { it.date }) {
        if (builder.isNotEmpty())
            builder.append("MESSAGE_SEP")

        builder.append(message.toString())
    }

    return builder.toString()
}

private fun Cursor.getMessageData(context: Context, from: String, date: Long = getLong(getColumnIndex("date"))): Message {
    val id = getString(getColumnIndex("_id"))
    val address = (if (from.isEmpty()) id.getSingleAddress(context) else from).formatPhoneNumber()
    val name = address.getName(context)
    val body = getString(getColumnIndex("body"))

    return if (body != null)
        Message(name, address, body, name.getColor(context), getString(getColumnIndex("type")), date, "")
    else {
        val parts = id.getMMSBody(context)

        var mms = ""

        for ((it, part) in parts[2].split(" ").withIndex())
            if (part.contains("image")) {
                mms = parts[1].split(" ")[it]
                break
            }

        Message(name, address, parts[0], name.getColor(context), if (getString(getColumnIndex("m_type")) == "132") "1" else "2", date * 1000, mms)
    }
}

private fun Context.getGroupAddress(id: String): String {
    val addressCursor = contentResolver.query(MessageFormat.format("content://mms/{0}/addr", id).toUri(), arrayOf("address"), "msg_id = $id", null, null)

    return if (addressCursor != null && addressCursor.moveToFirst()) {
        val address = DatabaseUtility.getGroupAddress(addressCursor)
        addressCursor.close()
        address
    } else
        ""
}

private fun String.getSingleAddress(context: Context): String {
    val addressBuilder = Telephony.Mms.CONTENT_URI.buildUpon()
    addressBuilder.appendPath(this).appendPath("addr")
    val singleAddress = context.contentResolver.query(addressBuilder.build(), arrayOf("address", "TYPE", Telephony.Mms.Addr.CHARSET), Telephony.Mms.Addr.TYPE + " = " + PduHeaders.FROM, null, null)
    singleAddress?.moveToFirst()
    val from = singleAddress?.getString(0) ?: ""

    return if (singleAddress != null && !TextUtils.isEmpty(from)) {
        val bytes = PduPersister.getBytes(from)
        val charset = singleAddress.getInt(1)
        singleAddress.close()
        EncodedStringValue(charset, bytes).string
    } else {
        singleAddress?.close()
        ""
    }
}

private fun String.getGroupNames(context: Context): String {
    val numList = this.split(",")

    return if (numList.isNotEmpty()) {
        val name = StringBuilder(numList[0].getName(context))

        for (i in 1 until numList.size)
            name.append(", ").append(numList[i].getName(context))

        name.toString()
    } else
        "Error finding group name"
}

private fun String.getMessageBody(context: Context): String {
    val data = context.contentResolver.query("content://mms/part".toUri(), null, "mid = $this", null, null)

    return if (data != null && data.moveToFirst()) {
        val parts = DatabaseUtility.getAllMmsParts(data, context)
        parts[0]
    } else
        ""
}

private fun String.getMMSBody(context: Context): Array<String> {
    val data = context.contentResolver.query("content://mms/part".toUri(), null, "mid = $this", null, null)

    return if (data != null && data.moveToFirst())
        DatabaseUtility.getAllMmsParts(data, context)
    else
        arrayOf("", "", "")
}

private data class Message(val name: String, val address: String, val body: String, val color: Int, val type: String, val date: Long, val mms: String) : Comparable<Message> {

    override fun compareTo(other: Message): Int {
        return when {
            date > other.date -> 1
            date < other.date -> -1
            else -> 0
        }
    }

    override fun toString(): String {
        return StringBuilder().append(name).append("ITEM_SEP").append(address).append("ITEM_SEP").append(body).append("ITEM_SEP").append(color).append("ITEM_SEP")
                .append(type).append("ITEM_SEP").append(date).append("ITEM_SEP").append(mms).toString()
    }
}