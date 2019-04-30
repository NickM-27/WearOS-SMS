package com.nick.mowen.wearossms.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.databinding.CustomRowDemoFromBinding
import com.nick.mowen.wearossms.databinding.CustomRowDemoToBinding
import com.nick.mowen.wearossms.databinding.FragmentDemoListBinding
import com.nick.mowen.wearossms.extension.getConversation

class DemoConversationFragment : AbstractFragment() {

    override lateinit var binding: FragmentDemoListBinding

    override fun bindView(layoutInflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_demo_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.demoList.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
            adapter = DemoAdapter(context, arguments?.getString("threadId") ?: "0")
            setHasFixedSize(true)
            isVerticalScrollBarEnabled = true
        }
    }

    data class DemoData(val body: String, val type: Int, val mms: Uri) {

        companion object {

            fun parse(data: String): ArrayList<DemoData> {
                val list = arrayListOf<DemoData>()

                for (item in data.split("MESSAGE_SEP")) {
                    val message = item.split("ITEM_SEP")

                    if (message.size >= 2)
                        list.add(DemoData(message[2], message[4].toInt(), message[6].toUri()))
                    else
                        list.add(DemoData("Error, found: ${message[0]}", 1, "".toUri()))
                }

                return list
            }
        }
    }

    private class DemoAdapter(context: Context, threadId: String) : RecyclerView.Adapter<DemoAdapter.AbstractDemoViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)
        private val data = DemoData.parse(context.getConversation(threadId)).reversed()

        override fun getItemViewType(position: Int): Int {
            return data[position].type
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractDemoViewHolder {
            return when (viewType) {
                1 -> OutgoingDemoViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.custom_row_demo_from, parent, false))
                else -> IncomingDemoViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.custom_row_demo_to, parent, false))
            }
        }

        override fun onBindViewHolder(model: AbstractDemoViewHolder, position: Int) = model.bindData(data[position])

        override fun getItemCount(): Int = data.size

        private abstract class AbstractDemoViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

            abstract fun bindData(data: DemoData)
        }

        private class IncomingDemoViewHolder(private val binding: CustomRowDemoToBinding) : AbstractDemoViewHolder(binding) {

            override fun bindData(data: DemoData) {
                binding.data = data
            }
        }

        private class OutgoingDemoViewHolder(private val binding: CustomRowDemoFromBinding) : AbstractDemoViewHolder(binding) {

            override fun bindData(data: DemoData) {
                binding.data = data
            }
        }
    }
}