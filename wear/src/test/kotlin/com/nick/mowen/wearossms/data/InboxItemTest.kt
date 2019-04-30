package com.nick.mowen.wearossms.data

import org.junit.Test

class InboxItemTest {

    @Test
    fun testCreation() {
        val item = InboxItem(arrayOf("", "", "", "", "", "false"))
        require(!item.incognito)
    }
}