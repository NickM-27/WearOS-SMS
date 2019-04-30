package com.nick.mowen.wearossms.binding

import android.graphics.PorterDuff
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.extension.getColor

object BindingAdapter {

    @BindingAdapter("mms")
    @JvmStatic
    fun setMms(view: AppCompatImageView, uri: Uri) {
        if (uri.toString().isNotEmpty()) {
            view.visibility = View.VISIBLE
            view.setImageURI(uri)
        } else
            view.visibility = View.GONE
    }

    @BindingAdapter("color")
    @JvmStatic
    fun setColor(view: ImageView, name: String) {
        val palette = ContextCompat.getDrawable(view.context, R.drawable.ic_color_palette)!!.mutate()
        palette.setColorFilter(name.getColor(view.context), PorterDuff.Mode.SRC_IN)
        view.setImageDrawable(palette)
    }
}