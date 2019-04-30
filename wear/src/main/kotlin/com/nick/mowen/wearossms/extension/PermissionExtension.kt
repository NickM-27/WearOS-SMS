package com.nick.mowen.wearossms.extension

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.nick.mowen.wearossms.Constants

fun Context.checkContactPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
}

fun Activity.getContactPermission() {
    if (!checkContactPermission()) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS))
            Toast.makeText(this, "Contacts permission is required to show images", Toast.LENGTH_LONG).show()

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS), Constants.REQ_PERMISSION_STARTUP)
    }
}