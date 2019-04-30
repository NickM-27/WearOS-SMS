package com.nick.mowen.wearossms.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.adapter.ContactListAdapter
import com.nick.mowen.wearossms.extension.getNumberFromId

class ContactPickerActivity : AbstractWearActivity() {

    private lateinit var contactList: WearableRecyclerView
    private lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        bindViews()
        bindData()
        contactList.setHasFixedSize(true)
        contactList.layoutManager = WearableLinearLayoutManager(this)
        contactList.isEdgeItemsCenteringEnabled = true
        contactList.adapter = ContactListAdapter(this, cursor)
        contactList.addOnItemTouchListener(Constants.ItemClickListener(this, contactList, object : Constants.ItemClickListener.ClickListener {

            override fun onClick(view: View?, position: Int) {
                cursor.moveToPosition(position)
                val address = getNumberFromId(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)), true)
                setResult(Activity.RESULT_OK, Intent().apply { putExtra("ADDRESS", address) })
                finishAfterTransition()
            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))
    }

    private fun bindViews() {
        contactList = findViewById(R.id.recipients_list)
    }

    private fun bindData() {
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY), null, null, "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC")

        if (!cursor.moveToFirst())
            finishAfterTransition()
    }

    override fun onStop() {
        if (!cursor.isClosed)
            cursor.close()

        super.onStop()
    }
}