package com.nick.mowen.wearossms.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.databinding.ActivityMainBinding

class MainActivity : AbstractSMSActivity() {

    override lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.NoActionBar)
        super.onCreate(savedInstanceState)
        bindViews()
    }

    override fun bindViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
    }

    fun openGitHub(@Suppress("UNUSED_PARAMETER") view: View?) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = "https://github.com/NickM-27/WearOS-SMS".toUri()
        })
    }
}