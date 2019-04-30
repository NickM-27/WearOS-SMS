package com.nick.mowen.wearossms.adapter

import android.database.Cursor
import android.graphics.Color
import android.provider.ContactsContract
import androidx.recyclerview.widget.RecyclerView
import android.support.wearable.activity.WearableActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.WearOSSMS
import com.nick.mowen.wearossms.extension.getNumberFromId
import com.nick.mowen.wearossms.extension.setConversationPhoto

class ContactListAdapter(private val context: WearableActivity, private val contacts: Cursor) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val idIndex = contacts.getColumnIndex(ContactsContract.Contacts._ID)
    private val nameIndex = contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContactViewHolder(layoutInflater.inflate(R.layout.custom_row_contact, parent, false))
    }

    override fun onBindViewHolder(model: RecyclerView.ViewHolder, position: Int) {
        contacts.moveToPosition(position)
        val holder = model as ContactViewHolder
        val address = context.getNumberFromId(contacts.getString(idIndex))
        holder.profile.setConversationPhoto(WearOSSMS.memoryCache, address, Color.GRAY.toString())
        holder.name.text = contacts.getString(nameIndex)
    }

    override fun getItemCount(): Int {
        return contacts.count
    }

    internal class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profile: ImageView = itemView.findViewById(R.id.row_profile)
        val name: TextView = itemView.findViewById(R.id.row_name)
    }
}