package com.nick.mowen.wearossms.adapter

import android.graphics.Color
import android.os.AsyncTask
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.support.wearable.activity.WearableActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.WearOSSMS
import com.nick.mowen.wearossms.data.InboxItem
import com.nick.mowen.wearossms.extension.checkContactPermission
import com.nick.mowen.wearossms.extension.setConversationPhoto
import java.util.*

class InboxAdapter(private val context: WearableActivity, private val data: ArrayList<InboxItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val memoryCache = WearOSSMS.memoryCache
    var blackTheme = false
    var ambient: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConversationViewHolder(layoutInflater.inflate(R.layout.custom_row_conversation, parent, false), blackTheme)
    }

    override fun onBindViewHolder(model: RecyclerView.ViewHolder, position: Int) {
        val holder = model as ConversationViewHolder
        val inbox = data[position]

        if (!inbox.incognito) {
            if (!ambient && context.checkContactPermission()) {
                val task = holder.profile.tag as AsyncTask<*, *, *>?

                if (task != null && task.status != AsyncTask.Status.FINISHED)
                    task.cancel(true)

                holder.profile.setConversationPhoto(memoryCache, inbox.address, inbox.color)
            }

            holder.name.text = inbox.name
            holder.message.text = inbox.message
        } else {
            if (!ambient)
                holder.profile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person_outline))

            holder.name.text = context.getString(R.string.title_incognito_hidden)
            holder.message.text = ""
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(item: ArrayList<InboxItem>) {
        animateItemsTo(item)
    }

    fun getItem(pos: Int): InboxItem {
        return data[pos]
    }

    private fun animateItemsTo(model: ArrayList<InboxItem>) {
        applyAndAnimateRemovals(model)
        applyAndAnimateAdditions(model)
        applyAndAnimateMovedItems(model)
    }

    private fun applyAndAnimateRemovals(newModels: ArrayList<InboxItem>) {
        for (i in data.indices.reversed()) {
            val model = data[i]
            if (!newModels.contains(model)) {
                data.removeAt(i)
                notifyItemRemoved(i)
            }
        }
    }

    private fun applyAndAnimateAdditions(newModels: ArrayList<InboxItem>) {
        var i = 0
        val count = newModels.size
        while (i < count) {
            val model = newModels[i]
            if (!data.contains(model)) {
                data.add(i, model)
                notifyItemInserted(i)
            }
            i++
        }
    }

    private fun applyAndAnimateMovedItems(newModels: ArrayList<InboxItem>) {
        for (toPosition in newModels.indices.reversed()) {
            val model = newModels[toPosition]
            val fromPosition = data.indexOf(model)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(data, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(data, i, i - 1)
                    }
                }
                notifyItemMoved(fromPosition, toPosition)
            }
        }
    }

    internal class ConversationViewHolder(itemView: View, blackTheme: Boolean) : RecyclerView.ViewHolder(itemView) {

        val profile: ImageView = itemView.findViewById(R.id.row_profile)
        val name: TextView = itemView.findViewById(R.id.row_name)
        val message: TextView = itemView.findViewById(R.id.row_text)

        init {
            if (blackTheme)
                itemView.findViewById<View>(R.id.row_container).setBackgroundColor(Color.BLACK)
        }
    }
}