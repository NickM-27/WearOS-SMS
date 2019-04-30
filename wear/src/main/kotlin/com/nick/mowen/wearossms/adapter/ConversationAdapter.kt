package com.nick.mowen.wearossms.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.WearOSSMS
import com.nick.mowen.wearossms.data.ConversationItem
import com.nick.mowen.wearossms.extension.*
import com.nick.mowen.wearossms.ui.MMSActivity
import java.io.File

class ConversationAdapter(private val context: Activity, messages: ArrayList<ConversationItem>) : RecyclerView.Adapter<ConversationAdapter.SMSViewHolder>(), View.OnClickListener {

    private val layoutInflater = LayoutInflater.from(context)
    private var outgoingColor = context.getMessageColor()
    var messages = messages
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var ambient = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int = messages[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSViewHolder =
            SMSViewHolder(layoutInflater.inflate(R.layout.custom_row_message, parent, false), this)

    override fun onBindViewHolder(holder: SMSViewHolder, position: Int) {
        if (!ambient) {
            val messageColor = if (getItemViewType(position) == 1) messages[position].color else ContextCompat.getColor(context, R.color.colorPrimary)

            holder.itemView.setBackgroundColor(if (messages[position].type == 1)
                messageColor.getMessageBackground()
            else
                outgoingColor)

            if (messages[position].type == 1)
                holder.profile.setConversationPhoto(WearOSSMS.memoryCache, messages[position].address, context.getAppColor().toString())
            else
                holder.profile.setProfilePhoto(WearOSSMS.memoryCache, File(context.filesDir, "/profile.png").absolutePath)

            holder.name.setTextColor(messageColor)

            if (messages[position].mms == "")
                holder.mms.visibility = View.GONE
            else {
                holder.mms.visibility = View.VISIBLE
                holder.mms.tag = messages[position].mms
            }
        } else {
            holder.itemView.setBackgroundColor(Color.BLACK)
            holder.name.setTextColor(Color.WHITE)
        }

        holder.name.text = (if (messages[position].type == 2) "Me" else messages[position].name) + " • " + messages[position].date.getTimeOfDay()
        holder.message.text = messages[position].message
    }

    override fun getItemCount(): Int = messages.size

    override fun onClick(view: View) {
        context.startActivity(Intent(context, MMSActivity::class.java).apply { data = view.tag.toString().toUri() })
    }

    inner class SMSViewHolder(itemView: View, click: View.OnClickListener) : RecyclerView.ViewHolder(itemView) {

        val profile: ImageView = itemView.findViewById(R.id.row_profile)
        val name: TextView = itemView.findViewById(R.id.row_name)
        val message: TextView = itemView.findViewById(R.id.row_message)
        val mms: TextView = itemView.findViewById(R.id.row_mms)

        init {
            mms.setOnClickListener(click)
        }
    }
}