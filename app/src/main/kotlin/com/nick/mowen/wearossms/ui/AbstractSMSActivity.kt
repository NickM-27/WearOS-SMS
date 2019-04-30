package com.nick.mowen.wearossms.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.nick.mowen.wearossms.extension.activateTheme

abstract class AbstractSMSActivity : AppCompatActivity() {

    protected abstract val binding: ViewDataBinding
    protected var dark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        dark = activateTheme()
        super.onCreate(savedInstanceState)
    }

    protected abstract fun bindViews()
}