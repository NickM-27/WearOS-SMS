package com.nick.mowen.wearossms.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nick.mowen.wearossms.Constants
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.databinding.CustomRowReplyBinding
import com.nick.mowen.wearossms.databinding.FragmentPersonRepliesBinding
import com.nick.mowen.wearossms.extension.textInputDialog

class PersonRepliesFragment : AbstractFragment() {

    override lateinit var binding: FragmentPersonRepliesBinding
    private lateinit var adapter: RepliesAdapter

    override fun bindView(layoutInflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_person_replies, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.repliesList.apply {
            this@PersonRepliesFragment.adapter = RepliesAdapter(requireActivity(), arguments!!.getString("threadId")
                    ?: "").apply {
                registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        if (adapter?.itemCount ?: 0 > 0)
                            binding.emptyGroup.isVisible = false
                    }

                    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                        if (adapter?.itemCount == 0)
                            binding.emptyGroup.isVisible = true
                    }
                })
            }
            adapter = this@PersonRepliesFragment.adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addOnItemTouchListener(Constants.ItemClickListener(context, this, object : Constants.ItemClickListener.ClickListener {

                override fun onClick(view: View?, position: Int) {

                }

                override fun onLongClick(view: View?, position: Int) {
                    AlertDialog.Builder(context)
                            .setTitle(R.string.confirm_delete_reply)
                            .setPositiveButton(getString(R.string.action_delete).toUpperCase()) { _, _ ->
                                this@PersonRepliesFragment.adapter.deleteReply(position)
                            }.setNegativeButton(R.string.action_cancel, null)
                            .show()
                }
            }))

            if (adapter?.itemCount ?: 0 > 0)
                binding.emptyGroup.isVisible = false
        }
        binding.fab.setOnClickListener { _ ->
            requireActivity().textInputDialog {
                adapter.addReply(it)
            }
        }
    }

    private class RepliesAdapter(private val context: Activity, private val threadId: String) : RecyclerView.Adapter<RepliesAdapter.RepliesViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)
        private val replyPreferences = context.getSharedPreferences(Constants.REPLY_PREFERENCES, Context.MODE_PRIVATE)
        private val list = ArrayList(replyPreferences.getStringSet(threadId, setOf()) ?: setOf())
        private val edit: (Int) -> (Unit) = { position ->
            context.textInputDialog(list[position]) {
                list[position] = it
                notifyItemChanged(position)
                commitList()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepliesViewHolder =
                RepliesViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.custom_row_reply, parent, false), edit)

        override fun onBindViewHolder(holder: RepliesViewHolder, position: Int) {
            list[position].let {
                holder.bindData(it)
            }
        }

        override fun getItemCount(): Int = list.size

        private fun commitList() {
            replyPreferences.edit { putStringSet(threadId, list.toHashSet()) }
        }

        fun addReply(reply: String) {
            list.add(reply)
            notifyItemInserted(list.size)
            commitList()
        }

        fun deleteReply(position: Int): String {
            val reply = list.removeAt(position)
            notifyItemRemoved(position)
            commitList()
            return reply
        }

        internal class RepliesViewHolder(private val binding: CustomRowReplyBinding, private val listener: (Int) -> (Unit)) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

            init {
                binding.root.setOnClickListener(this)
            }

            override fun onClick(view: View?) = listener(adapterPosition)

            fun bindData(text: String) {
                binding.reply = text
                binding.executePendingBindings()
            }
        }
    }
}