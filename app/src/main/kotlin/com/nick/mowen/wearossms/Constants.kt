package com.nick.mowen.wearossms

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

object Constants {

    //Codes for permissions and actions
    const val REQ_CODE_IMAGE_SELECT = 8
    const val REQ_CODE_SETTINGS = 88

    const val REQ_PERMISSION_STORAGE = 8
    const val REQ_PERMISSION_SMS = 888

    //Shared Preferences
    const val REPLY_PREFERENCES = "reply_preferences"
    const val COLOR_PREFERENCES = "color_preferences"

    //Android wear capability
    const val TEXT_CAPABILITY = "message_acceptable"
    const val CONVERSATION_CAPABILITY = "conversation_acceptable"
    const val PREF_CAPABILITY = "preferences_sync"

    //Click listener for recycler views
    class ItemClickListener(context: Context, recyclerView: RecyclerView?, clickListener: ItemClickListener.ClickListener?) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val child = recyclerView?.findChildViewUnder(e.x, e.y)

                    if (child != null && clickListener != null)
                        clickListener.onClick(child, recyclerView.getChildLayoutPosition(child))

                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView?.findChildViewUnder(e.x, e.y)

                    if (child != null && clickListener != null)
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