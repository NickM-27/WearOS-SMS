package com.nick.mowen.wearossms

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

object Constants {

    const val REQ_CODE_CONTACT = 8
    const val REQ_CODE_MESSAGE = 88

    const val REQ_PERMISSION_STARTUP = 8

    //Constants for preferences

    const val HISTORY_PREFERENCES = "historyPreferences"
    const val REPLY_PREFERENCES = "replyPreferences"

    //Capability to request inbox

    const val MESSAGE_CAPABILITY = "message_retrieval"
    const val CONVERSATION_CAPABILITY = "conversation_retrieval"
    const val OPENING_CAPABILITY = "conversation_opening"
    const val SENDING_CAPABILITY = "message_sending"
    const val CALLING_CAPABILITY = "call_making"
    const val MMS_CAPABILITY = "mms_retrieval"

    //Name of saved message list

    const val INBOX_LIST = "inbox_list"

    class ItemClickListener(context: Context, recyclerView: RecyclerView, clickListener: ItemClickListener.ClickListener) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)

                    if (child != null)
                        clickListener.onClick(child, recyclerView.getChildLayoutPosition(child))

                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)

                    if (child != null)
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child))
                }
            })
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(e)
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }

        interface ClickListener {
            fun onClick(view: View?, position: Int)

            fun onLongClick(view: View?, position: Int)
        }
    }
}