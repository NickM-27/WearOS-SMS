package com.nick.mowen.wearossms.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

abstract class AbstractSMSActivity : AppCompatActivity() {

    protected abstract val binding: ViewDataBinding

    protected abstract fun bindViews()
}