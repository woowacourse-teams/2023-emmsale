package com.emmsale.presentation.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<V : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int,
) : AppCompatActivity() {

    protected val binding: V by lazy {
        DataBindingUtil.inflate(layoutInflater, layoutResId, null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
    }
}
