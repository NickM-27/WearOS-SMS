package com.nick.mowen.wearossms.adapter

import androidx.recyclerview.widget.RecyclerView
import android.support.wearable.activity.WearableActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.WearOSSMS
import com.nick.mowen.wearossms.data.ConversationItem
import com.nick.mowen.wearossms.extension.setConversationPhoto

class RecipientListAdapter(private val context: WearableActivity, private val list: ArrayList<ConversationItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    init {
        val filter = LinkedHashSet<ConversationItem>()
        filter.addAll(list)
        list.clear()
        list.addAll(filter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecipientViewHolder(layoutInflater.inflate(R.layout.custom_row_recipient, parent, false))
    }

    override fun onBindViewHolder(model: RecyclerView.ViewHolder, position: Int) {
        val holder = model as RecipientViewHolder
        holder.name.text = list[position].name
        holder.number.text = list[position].address
        holder.profile.setConversationPhoto(WearOSSMS.memoryCache, list[position].address, list[position].color.toString())
        holder.profile.setTag(R.integer.item_holder, position.toString())
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal class RecipientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profile: ImageView = itemView.findViewById(R.id.row_profile)
        var name: TextView = itemView.findViewById(R.id.row_name)
        var number: TextView = itemView.findViewById(R.id.row_number)
    }
}