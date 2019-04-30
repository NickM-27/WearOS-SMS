package com.nick.mowen.wearossms.extension

import java.util.*

/**
 * Formats phone number to get rid of characters that are not numbers
 *
 * @return formatted number
 */
fun String.formatPhoneNumber(): String = this.replace("+1", "")
        .replace("-", "")
        .replace("(", "")
        .replace(")", "")
        .replace(" ", "")

/**
 * Creates a unique identifier for a group conversation
 *
 * @return unique identifier
 */
fun String.createUniqueIdentifier(): String {
    val data = this.formatPhoneNumber().replace(",", "")
    val numbers = data.split("")
    Collections.sort(numbers)
    val id = StringBuilder()

    for (num in numbers)
        id.append(num)

    return id.toString()
}