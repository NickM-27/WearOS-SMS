package com.nick.mowen.wearossms.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class AbstractFragment : Fragment() {

    protected abstract val binding: ViewDataBinding

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = bindView(inflater, container)

    abstract fun bindView(layoutInflater: LayoutInflater, container: ViewGroup?): View
}