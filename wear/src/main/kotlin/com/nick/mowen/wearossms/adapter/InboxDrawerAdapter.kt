package com.nick.mowen.wearossms.adapter

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import android.support.wearable.activity.WearableActivity
import com.nick.mowen.wearossms.R

class InboxDrawerAdapter(private val context: WearableActivity) : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {

    override fun getItemText(pos: Int): CharSequence {
        return items[pos]
    }

    override fun getItemDrawable(pos: Int): Drawable {
        return ContextCompat.getDrawable(context, icons[pos])!!
    }

    override fun getCount(): Int {
        return items.size
    }

    companion object {

        private val items = arrayOf("Inbox", "Settings")
        private val icons = arrayOf(R.drawable.ic_inbox, R.drawable.ic_settings)
    }
}