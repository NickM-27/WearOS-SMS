package com.nick.mowen.wearossms.view

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder

@Suppress("unused")
class ColorPreference : Preference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val color = PreferenceManager.getDefaultSharedPreferences(context).getInt("accentColor", -1)

        if (color != -1) {
            val imageView = holder?.findViewById(android.R.id.icon) as ImageView?

            if (imageView?.drawable != null)
                imageView.drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}