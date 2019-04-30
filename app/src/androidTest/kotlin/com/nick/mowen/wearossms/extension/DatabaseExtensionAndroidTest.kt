package com.nick.mowen.wearossms.extension

import android.preference.PreferenceManager
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class DatabaseExtensionAndroidTest {

    private class InboxItem private constructor(var name: String, var address: String, var message: String, var color: String, var thread: String, hidden: String) {

        var incognito: Boolean = false

        init {
            incognito = hidden.toBoolean()
        }

        constructor(item: Array<String>) : this(item[0], item[1], item[2], item[3], item[4], item[5])
    }

    private fun String.getInboxData(): ArrayList<InboxItem> {
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

    @Test
    fun testInbox() {
        val context = InstrumentationRegistry.getTargetContext()
        val data = "${PreferenceManager.getDefaultSharedPreferences(context).getBoolean("premium", false)}PREMIUM_SEP${context.getInbox()}"
        val lists = data.split("PREMIUM_SEP")
        require(lists[0] == "true" || lists[0] == "false")
        val inbox = lists[1].getInboxData()
        require(inbox.size == 3)
    }

    @Test
    fun testBackupInbox() {
        val context = InstrumentationRegistry.getTargetContext()
        val data = "${PreferenceManager.getDefaultSharedPreferences(context).getBoolean("premium", false)}PREMIUM_SEP${context.getInboxBackup()}"
        val lists = data.split("PREMIUM_SEP")
        require(lists[0] == "true" || lists[0] == "false")
        val inbox = lists[1].getInboxData()
        require(inbox.size == 3)
    }

    @Test
    fun testConversation() {
        val context = InstrumentationRegistry.getTargetContext()
        val list = context.getInbox().getInboxData()

        for (it in list)
            context.getConversation(it.thread)
    }
}