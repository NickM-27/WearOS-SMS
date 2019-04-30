package com.nick.mowen.wearossms.extension

import android.text.format.DateFormat
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Calculates correct [SimpleDateFormat] to use in the inbox
 *
 * @return formatted [String]
 */
fun Long.getInboxTime(): String {
    return if (DateUtils.isToday(this) || isYesterday())
        SimpleDateFormat("h:mm a", Locale.US).format(Date(this))
    else if (isThisWeek())
        SimpleDateFormat("EEE", Locale.US).format(Date(this))
    else
        SimpleDateFormat("MMM d", Locale.US).format(Date(this))
}

/**
 * Calculates correct [SimpleDateFormat] to use for time scenario
 *
 * @return formatted [String]
 */
fun Long.getTimeOfDay(): String {
    return if (DateUtils.isToday(this) || isYesterday())
        SimpleDateFormat("h:mm a", Locale.US).format(Date(this))
    else if (isThisWeek())
        SimpleDateFormat("EEE h:mm a", Locale.US).format(Date(this))
    else
        SimpleDateFormat("MMM d, h:mm a", Locale.US).format(Date(this))
}

/**
 * Calculates whether a [Date] is considered to be yesterday
 *
 * @return if yesterday
 */
fun Long.isYesterday(): Boolean {
    val now = Calendar.getInstance()
    val message = Calendar.getInstance()
    message.timeInMillis = this
    now.add(Calendar.DATE, -1)
    return (now.get(Calendar.YEAR) == message.get(Calendar.YEAR)
            && now.get(Calendar.MONTH) == message.get(Calendar.MONTH)
            && now.get(Calendar.DATE) == message.get(Calendar.DATE))
}

/**
 * Formats the current date with time
 *
 * @return formatted [String]
 */
fun Long.formatDate(): String {
    val locale = Locale.getDefault()
    return SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "M/d/yyyy h:mm a"), locale).format(Date(this))
}

/**
 * Formats the current date
 *
 * @return formatted [String]
 */
fun Long.calendarDate(): String {
    val locale = Locale.getDefault()
    return SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "M/d/yyyy"), locale).format(Date(this))
}

/**
 * Checks if epoch is in the past or future
 */
fun Long.checkFuture(): Boolean {
    val cal = Calendar.getInstance()
    return cal.timeInMillis < this
}

/**
 * Calculates whether a [Date] is within the last seven days
 *
 * @return if within last 7 days
 */
fun Long.isThisWeek(): Boolean {
    val question = Date(this)
    val c = Calendar.getInstance(Locale.US)
    c.firstDayOfWeek = c.get(Calendar.DAY_OF_WEEK)
    c.add(Calendar.WEEK_OF_YEAR, -1)
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    val monday = c.time
    val nextMonday = Date(monday.time + (7 * 24 * 60 * 60 * 1000))
    return question.after(monday) && question.before(nextMonday)
}