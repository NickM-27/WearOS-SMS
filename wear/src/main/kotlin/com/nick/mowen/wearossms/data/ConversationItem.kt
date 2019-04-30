package com.nick.mowen.wearossms.data

data class ConversationItem(private val raw: Array<String>) : Comparable<ConversationItem> {

    var name: String = raw[0]
    var address: String = raw[1]
    var message: String = raw[2]
    var mms: String
    var color: Int = 0
    var type: Int = 0
    var date: Long = 0

    init {
        color = raw[3].toInt()
        type = raw[4].toInt()
        date = raw[5].toLong()

        mms = if (raw.size == 7)
            raw[6]
        else
            ""
    }

    override operator fun compareTo(other: ConversationItem): Int {
        return if (other.address == address)
            0
        else
            address.compareTo(other.address)
    }

    override fun equals(other: Any?): Boolean {
        val point = other as ConversationItem?
        return point!!.address == address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }

    override fun toString(): String {
        return address
    }
}