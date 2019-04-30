package com.nick.mowen.wearossms.view

import android.content.Context
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder

@Suppress("unused")
class ColorPreferenceCategory : PreferenceCategory {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val color = PreferenceManager.getDefaultSharedPreferences(context).getInt("accentColor", -1)

        if (color != -1) {
            val titleView = holder?.findViewById(android.R.id.title) as TextView
            titleView.setTextColor(color)
        }
    }
}