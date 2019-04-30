package com.nick.mowen.wearossms.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.nick.mowen.wearossms.R
import com.nick.mowen.wearossms.databinding.FragmentMainBinding

class MainFragment : AbstractFragment() {

    override lateinit var binding: FragmentMainBinding

    override fun bindView(layoutInflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.premiumExplanation.text = getString(R.string.explanation_premium_bought)
        binding.premiumButton.text = getString(R.string.action_contact_developer)
    }

    private fun premium() {
        startActivity(Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("nick@nicknackdevelopment.com"))
            putExtra(Intent.EXTRA_SUBJECT, "WearOS SMS Feedback")
            putExtra(Intent.EXTRA_TEXT, "I just wanted to tell you...")
        })
    }
}