package com.nick.mowen.wearossms.adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.extension.toHex

class DotAdapter(context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val colors = getColors()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            DotViewHolder(layoutInflater.inflate(R.layout.view_dot, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DotViewHolder).dot.background.colorFilter = PorterDuffColorFilter(colors[position], PorterDuff.Mode.SRC_IN)
    }

    override fun getItemCount(): Int = colors.size

    private inner class DotViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dot: TextView = itemView as TextView
    }

    companion object {

        private fun getColors(): IntArray =
                intArrayOf(Color.parseColor("#b8c847"), Color.parseColor("#67bb43"), Color.parseColor("#41b691"), Color.parseColor("#009688"), Color.parseColor("#00BCD4"), Color.parseColor("#4182b6"), Color.parseColor("#4149b6"), Color.parseColor("#7641b6"), Color.parseColor("#b741a7"), Color.parseColor("#B388FF"), Color.parseColor("#9E9E9E"), Color.parseColor("#212121"), Color.parseColor("#D50000"), Color.parseColor("#c54657"), Color.parseColor("#F44336"), Color.parseColor("#d1694a"), Color.parseColor("#d1904a"), Color.parseColor("#FF9800"), Color.parseColor("#d1c54a"))

        fun getColor(position: Int): String = getColors()[position].toHex()

    }
}