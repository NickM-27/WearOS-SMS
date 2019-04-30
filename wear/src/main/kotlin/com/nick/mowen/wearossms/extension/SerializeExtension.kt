package com.nick.mowen.wearossms.extension

import com.nick.mowen.wearossms.data.ConversationItem
import com.nick.mowen.wearossms.data.InboxItem
import java.util.*

fun String.getInboxData(): ArrayList<InboxItem> {
    val rawList = split("MESSAGE_SEP")
    val items = ArrayList<InboxItem>()

    if (this == "")
        return items

    for (item in rawList) {
        val attrs = item.split("ITEM_SEP").toTypedArray()

        try {
            items.add(InboxItem(attrs))
        } catch (ignored: IndexOutOfBoundsException) {
            ignored.printStackTrace()
        }
    }

    return items
}

fun String.getConversationData(): ArrayList<ConversationItem> {
    val rawList = split("MESSAGE_SEP")
    val items = ArrayList<ConversationItem>()

    if (this == "")
        return items

    for (item in rawList) {
        val attrs = item.split("ITEM_SEP").toTypedArray()

        try {
            items.add(ConversationItem(attrs))
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    items.reverse()
    return items
}