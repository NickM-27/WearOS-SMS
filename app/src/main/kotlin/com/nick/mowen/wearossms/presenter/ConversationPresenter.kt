package com.nick.mowen.wearossms.presenter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.navigation.findNavController
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.extension.getColor
import com.nick.mowen.wearossms.extension.showColorPicker
import com.nick.mowen.wearossms.extension.toHex
import com.nick.mowen.wearossms.fragment.DemoInboxFragmentDirections

class ConversationPresenter {

    fun conversationClicked(view: View, threadId: String) {
        view.findNavController().run { navigate(DemoInboxFragmentDirections.actionDemoInboxFragmentToDemoConversationFragment(threadId)) }
    }

    fun colorClicked(view: ImageView, name: String) {
        val preferences = view.context.getSharedPreferences(Constants.COLOR_PREFERENCES, Context.MODE_PRIVATE)
        (view.context as Activity).showColorPicker(name.getColor(view.context, preferences).toHex()) {
            preferences.edit().putInt(name, it.toColorInt()).apply()
            Toast.makeText(view.context, "Updated Color", Toast.LENGTH_SHORT).show()
        }
    }
}