package com.nick.mowen.wearossms.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.databinding.CustomRowDemoConversationBinding
import com.nick.mowen.wearossms.databinding.FragmentDemoListBinding
import com.nick.mowen.wearossms.extension.getInbox
import com.nick.mowen.wearossms.presenter.ConversationPresenter

class DemoInboxFragment : AbstractFragment() {

    override lateinit var binding: FragmentDemoListBinding

    override fun bindView(layoutInflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_demo_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.demoList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DemoAdapter(context)
            setHasFixedSize(true)
        }
    }

    data class DemoData(val name: String, val address: String, val threadId: String) {

        companion object {

            fun parse(data: String): ArrayList<DemoData> {
                val list = arrayListOf<DemoData>()

                for (item in data.split("MESSAGE_SEP")) {
                    val message = item.split("ITEM_SEP")

                    if (message.size >= 2)
                        list.add(DemoData(message[0], message[1], message[4]))
                    else
                        list.add(DemoData("Error, found: ${message[0]}", "Unknown", "-1"))
                }

                return list
            }
        }
    }

    private class DemoAdapter(context: Context) : RecyclerView.Adapter<DemoAdapter.DemoViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)
        private val data = DemoData.parse(context.getInbox())
        private val presenter = ConversationPresenter()
        private val listener: (View, Int) -> Unit = { view, position ->
            when (view.id) {
                R.id.row_color -> presenter.colorClicked(view as ImageView, data[position].name)
                else -> presenter.conversationClicked(view, data[position].threadId)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder =
                DemoViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.custom_row_demo_conversation, parent, false), listener)

        override fun onBindViewHolder(holder: DemoViewHolder, position: Int) = holder.bindData(data[position])

        override fun getItemCount(): Int = data.size

        private class DemoViewHolder(private val binding: CustomRowDemoConversationBinding, private val listener: (View, Int) -> Any) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

            init {
                binding.root.setOnClickListener(this)
                binding.rowColor.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                if (view != null)
                    listener(view, adapterPosition)
            }

            fun bindData(data: DemoData) {
                binding.data = data
            }
        }
    }
}