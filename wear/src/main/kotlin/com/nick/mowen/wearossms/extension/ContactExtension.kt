package com.nick.mowen.wearossms.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

fun String?.getName(context: Context): String {
    if (this == null || this == "")
        return ""

    var name: String = this

    return if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(this))
        val cursor = context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
            cursor.close()
        }
        name
    } else
        ""
}

fun Context.getName(number: String?): String {
    if (number == null || number == "")
        return ""

    var name: String = number
    val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
    val cursor = contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)!!

    if (cursor.moveToFirst())
        name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))

    cursor.close()
    return name
}

fun Uri.getContactAddress(context: Context): String {
    val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
    val cursor = context.contentResolver.query(this, projection, null, null, null)

    return if (cursor != null && cursor.moveToFirst()) {
        val address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
        cursor.close()
        address
    } else
        "-1"
}

fun Context.getNumberFromId(id: String, skip: Boolean = false): String {
    var number = StringBuilder()

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
        val phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
        if (phone == null || !phone.moveToFirst())
            number = StringBuilder()
        else {

            if (phone.count == 1)
                number = StringBuilder(phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
            else {
                do {
                    val item = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).formatPhoneNumber()

                    if (!skip) {
                        if (!number.toString().contains(item))
                            number.append(item).append("\n")
                    } else
                        return item
                } while (phone.moveToNext())
            }
            phone.close()
        }
    }
    return number.toString()
}