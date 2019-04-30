package com.nick.mowen.wearossms.view

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.Switch
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference

@Suppress("unused")
class ColorSwitchPreference : SwitchPreference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val color = PreferenceManager.getDefaultSharedPreferences(context).getInt("accentColor", -1)

        if (color != -1) {
            val imageView = holder?.findViewById(android.R.id.icon) as ImageView?
            val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

            if (imageView?.drawable != null)
                imageView.drawable.colorFilter = colorFilter

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val switchView = holder?.findViewById(android.R.id.switch_widget) as Switch?

                if (switchView?.thumbDrawable != null)
                    switchView.thumbDrawable.colorFilter = colorFilter
            }
        }
    }
}