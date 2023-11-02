package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.databinding.LayoutNetworkErrorBinding
import com.emmsale.presentation.common.views.NetworkErrorView.OnRefreshListener
import kotlin.properties.Delegates.observable

class NetworkErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding: LayoutNetworkErrorBinding by lazy {
        LayoutNetworkErrorBinding.inflate(LayoutInflater.from(context), this, false)
    }

    var onRefreshListener: OnRefreshListener by observable(OnRefreshListener { }) { _, _, newValue ->
        binding.onRefreshListener = newValue
    }

    init {
        addView(binding.root)
        background = context.getColor(R.color.white).toDrawable()
        isClickable = true
    }

    fun interface OnRefreshListener {
        fun onRefresh()
    }
}

@BindingAdapter("app:onRefresh")
fun NetworkErrorView.setOnRefreshListener(onRefreshListener: OnRefreshListener) {
    this.onRefreshListener = onRefreshListener
}
