package com.nick.mowen.wearossms.data

import android.util.Log
import java.util.*

class InboxItem private constructor(var name: String, var address: String, var message: String, var color: String, var thread: String, hidden: String, list: String) {

    var incognito: Boolean = false
    var replies: Array<String>

    constructor(item: Array<String>) : this(item[0], item[1], item[2], item[3], item[4], item[5], item[6])

    init {
        incognito = hidden.toBoolean()
        replies = list.split(",").toTypedArray()

        Log.e("ERROR?", "$thread ${Arrays.toString(replies)}")
    }
}