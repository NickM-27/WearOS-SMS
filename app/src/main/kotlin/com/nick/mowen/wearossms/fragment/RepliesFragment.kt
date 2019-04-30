package com.nick.mowen.wearossms.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.databinding.CustomRowContactRepliesBinding
import com.nick.mowen.wearossms.databinding.FragmentRepliesBinding
import com.nick.mowen.wearossms.extension.getInbox

class RepliesFragment : AbstractFragment() {

    override lateinit var binding: FragmentRepliesBinding
    private lateinit var adapter: RepliesAdapter

    override fun bindView(layoutInflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_replies, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.repliesList.apply {
            layoutManager = LinearLayoutManager(context)
            this@RepliesFragment.adapter = RepliesAdapter(context)
            adapter = this@RepliesFragment.adapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            addOnItemTouchListener(Constants.ItemClickListener(context, this, object : Constants.ItemClickListener.ClickListener {

                override fun onClick(view: View?, position: Int) {
                    view?.findNavController()?.apply {
                        navigate(RepliesFragmentDirections.actionRepliesFragmentToPersonRepliesFragment(this@RepliesFragment.adapter.getThread(position)))
                    }
                }

                override fun onLongClick(view: View?, position: Int) {

                }
            }))
        }
    }

    private class RepliesAdapter(context: Context) : RecyclerView.Adapter<RepliesAdapter.RepliesViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)
        private val replyPreferences = context.getSharedPreferences(Constants.REPLY_PREFERENCES, Context.MODE_PRIVATE)
        private val list = Contact.getContactsList(context.getInbox())

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepliesViewHolder =
                RepliesViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.custom_row_contact_replies, parent, false))

        override fun onBindViewHolder(holder: RepliesViewHolder, position: Int) {
            list[position].let {
                holder.bindData(it.name, replyPreferences.getStringSet(it.threadId, setOf())?.size
                        ?: 0)
            }
        }

        override fun getItemCount(): Int = list.size

        fun getThread(position: Int): String = list[position].threadId

        internal class RepliesViewHolder(private val binding: CustomRowContactRepliesBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bindData(name: String, count: Int) {
                binding.replyName = name
                binding.replyCount = count
                binding.executePendingBindings()
            }
        }
    }

    private data class Contact(val name: String, val threadId: String) {

        companion object {

            fun getContactsList(data: String): List<Contact> {
                val list = arrayListOf<Contact>()

                for (item in data.split("MESSAGE_SEP")) {
                    val message = item.split("ITEM_SEP")

                    if (message.size >= 2)
                        list.add(Contact(message[0], message[4]))
                    else
                        list.add(Contact("Error", "-1"))
                }

                return list
            }
        }
    }
}