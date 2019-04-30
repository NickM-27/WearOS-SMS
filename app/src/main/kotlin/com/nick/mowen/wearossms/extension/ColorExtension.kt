package com.nick.mowen.wearossms.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cdflynn.android.library.turn.TurnLayoutManager
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.adapter.DotAdapter

/**
 * Gets the primary app color set by user
 *
 * @return Color [Int] of primary app color
 */
fun Context.getAppColor(): Int {
    val special = PreferenceManager.getDefaultSharedPreferences(this).getInt("primaryColor", -1)
    return if (special == -1) ContextCompat.getColor(this, R.color.colorPrimary) else special
}

/**
 * Gets the accent color set by user
 *
 * @return Color [Int] of accent color
 */
fun Context.getAccentColor(): Int {
    return PreferenceManager.getDefaultSharedPreferences(this).getInt("accentColor", -1)
}

/**
 * Gets the accent color set by user
 *
 * @return Color [Int] of accent color
 */
fun Context.requireAccentColor(): Int {
    val color = getAccentColor()

    return if (color == -1)
        ContextCompat.getColor(this, R.color.colorAccent)
    else
        color
}

/**
 * Turns Color [Int] into a string hex equivalent
 *
 * @return hex value of Color
 */
fun Int.toHex(): String = String.format("#%06X", 0xFFFFFF and this)

/**
 * Calculates the status bar color from the primary color
 *
 * @return Color [Int] for status bar
 */
fun Int.getStatusColor(): Int {
    var r = Color.red(this)
    var g = Color.green(this)
    var b = Color.blue(this)

    r -= r / 10
    g -= g / 10
    b -= b / 10

    return Color.rgb(Math.max(0, r), Math.max(0, g), Math.max(0, b))
}

/**
 * Gets Color [Int] from hex
 *
 * @return Color [Int] value
 */
fun String.toColor(): Int = Color.parseColor(this)

fun Activity.showColorPicker(current: String?, listener: (String) -> Unit) {
    val recyclerView = RecyclerView(this)
    recyclerView.layoutManager = TurnLayoutManager(this, resources.getDimension(R.dimen.color_offset).toInt(), 550)
    recyclerView.adapter = DotAdapter(this)

    val dialog = AlertDialog.Builder(this)
            .setView(recyclerView)
            .setPositiveButton(R.string.action_color_advanced) { dialogInterface, _ ->
                dialogInterface.dismiss()
                showAdvancedColorPicker(current, listener)
            }
            .setNeutralButton("Default") { dialog, _ ->
                listener("NONE")
                dialog.dismiss()
            }.show()

    recyclerView.addOnItemTouchListener(Constants.ItemClickListener(this, recyclerView, object : Constants.ItemClickListener.ClickListener {
        override fun onClick(view: View?, position: Int) {
            listener(DotAdapter.getColor(position))
            dialog.dismiss()
        }

        override fun onLongClick(view: View?, position: Int) {

        }
    }))
}

private fun Activity.showAdvancedColorPicker(current: String?, listener: (String) -> Unit) {
    val colorPicker = AlertDialog.Builder(this)
    val layout = LayoutInflater.from(this).inflate(R.layout.dialog_color_picker, LinearLayout(this), false)
    colorPicker.setView(layout)
            .setPositiveButton("Select") { dialog, _ ->
                val hex = layout.findViewById<TextView>(R.id.hex_val)
                listener(hex.text.toString())
                dialog.dismiss()
            }

    colorPicker.create()
    val tv = layout.findViewById<TextView>(R.id.textView)
    val redText = layout.findViewById<TextView>(R.id.redToolTip)
    val greenText = layout.findViewById<TextView>(R.id.greenToolTip)
    val blueText = layout.findViewById<TextView>(R.id.blueToolTip)
    val hexText = layout.findViewById<TextView>(R.id.hex_val)
    val red = layout.findViewById<SeekBar>(R.id.red)
    val green = layout.findViewById<SeekBar>(R.id.green)
    val blue = layout.findViewById<SeekBar>(R.id.blue)


    //red.getProgressDrawable().mutate().setColorFilter(new LightingColorFilter(Color.BLACK, toColor(this, R.color.color_red)));
    red.progressDrawable = ContextCompat.getDrawable(this, R.drawable.seek_bar_red)
    green.progressDrawable = ContextCompat.getDrawable(this, R.drawable.seek_bar_green)
    blue.progressDrawable = ContextCompat.getDrawable(this, R.drawable.seek_bar_blue)
    red.thumb.mutate().colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.color_red), PorterDuff.Mode.SRC_IN)
    blue.thumb.mutate().colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.color_blue), PorterDuff.Mode.SRC_IN)
    green.thumb.mutate().colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.color_green), PorterDuff.Mode.SRC_IN)

    if (current != null && current.isNotEmpty()) {
        val receivedColor: Int
        try {
            receivedColor = Color.parseColor(current)
            red.progress = Color.red(receivedColor)
            green.progress = Color.green(receivedColor)
            blue.progress = Color.blue(receivedColor)
        } catch (iae: IllegalArgumentException) {
            iae.printStackTrace()
        }
    }

    val r = red.progress
    val g = green.progress
    val b = blue.progress
    val redVal = r * (red.width / red.max)
    val greenVal = g * (green.width / green.max)
    val blueVal = b * (blue.width / blue.max)
    redText.text = r.toString()
    redText.x = red.x + redVal.toFloat() + red.thumbOffset.toFloat()
    greenText.text = g.toString()
    greenText.x = green.x + greenVal.toFloat() + green.thumbOffset.toFloat()
    blueText.text = b.toString()
    blueText.x = blue.x + blueVal.toFloat() + blue.thumbOffset.toFloat()
    tv.setBackgroundColor(Color.rgb(r, g, b))
    hexText.text = String.format("#%02x%02x%02x", r, g, b)
    colorPicker.show()

    val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            //add code here
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            //add code here
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val r1 = red.progress
            val g1 = green.progress
            val b1 = blue.progress
            val measure = Paint()
            when (seekBar.id) {
                R.id.red -> {
                    redText.text = progress.toString()
                    redText.x = seekBar.thumb.bounds.centerX() - 1.2.toFloat() * measure.measureText(redText.text.toString())
                }
                R.id.green -> {
                    greenText.text = progress.toString()
                    greenText.x = seekBar.thumb.bounds.centerX() - 1.2.toFloat() * measure.measureText(greenText.text.toString())
                }
                R.id.blue -> {
                    blueText.text = progress.toString()
                    blueText.x = seekBar.thumb.bounds.centerX() - 1.2.toFloat() * measure.measureText(blueText.text.toString())
                }
            }
            hexText.text = String.format("#%02x%02x%02x", r1, g1, b1)
            tv.setBackgroundColor(Color.rgb(r1, g1, b1))
        }
    }

    red.setOnSeekBarChangeListener(seekBarListener)
    green.setOnSeekBarChangeListener(seekBarListener)
    blue.setOnSeekBarChangeListener(seekBarListener)
}