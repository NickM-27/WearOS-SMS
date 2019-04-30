package com.nick.mowen.wearossms.extension

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R

/**
 * Requests user to grand SMS permission
 */
fun Activity.getStartupPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS), Constants.REQ_PERMISSION_SMS)
}

fun Context.checkStartupPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
}

/**
 * Requests user to grant location permission for theme, location reminders, and location sharing
 *
 * @return if permission is already granted
 */
fun Activity.getLocationPermission(): Boolean {
    return if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, getString(R.string.explanation_location_permission), Toast.LENGTH_LONG).show()
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        false
    } else
        true
}

/**
 * Check to see if app has storage permission
 *
 * @return if permission is granted
 */
fun Activity.checkStoragePermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}

/**
 * Requests user to grand storage permission
 *
 * @param req Permission request code to link back to activity
 * @param check whether or not to check for image if permission is granted
 */
fun Activity.getStoragePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            Toast.makeText(this, "Storage permission is needed to get image to send", Toast.LENGTH_LONG).show()

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), Constants.REQ_PERMISSION_STORAGE)
    }
}

/**
 * Starts activity for user to get image
 *
 * @param req Permission request code to link back to activity
 */
fun Activity.getImage(req: Int = Constants.REQ_CODE_IMAGE_SELECT) {
    Log.i("Permission Extension", "Starting standard image get activity")
    startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }, req)
}