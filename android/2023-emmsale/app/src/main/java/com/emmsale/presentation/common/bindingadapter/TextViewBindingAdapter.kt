package com.emmsale.presentation.common.bindingadapter

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.data.model.EventApplicationStatus
import com.emmsale.data.model.EventProgressStatus
import com.emmsale.data.model.OnOfflineMode
import com.emmsale.data.model.PaymentType

@BindingAdapter("android:text")
fun TextView.setNumberText(numberText: Int) {
    text = numberText.toString()
}

@BindingAdapter("app:paymentType")
fun TextView.setPaymentType(
    paymentType: PaymentType? = null,
) {
    if (paymentType == null) return

    text = when (paymentType) {
        PaymentType.FREE -> context.getString(R.string.all_free)
        PaymentType.PAID -> context.getString(R.string.all_paid)
        PaymentType.PAID_OR_FREE -> context.getString(R.string.all_paid_free)
    }
}

@BindingAdapter("app:eventApplicationStatus")
fun TextView.setEventApplicationStatus(
    applicationStatue: EventApplicationStatus? = null,
) {
    if (applicationStatue == null) return

    when (applicationStatue) {
        EventApplicationStatus.Ended -> {
            text = "신청 마감"
            setTextColor(ContextCompat.getColor(context, R.color.gray))
        }

        is EventApplicationStatus.InProgress -> {
            text = context.getString(
                R.string.all_application_in_progress,
                applicationStatue.remainingDays,
            )
            setTextColor(ContextCompat.getColor(context, R.color.primary_color))
        }

        is EventApplicationStatus.UpComing -> {
            text = context.getString(
                R.string.all_application_up_coming,
                applicationStatue.remainingDays,
            )
            setTextColor(ContextCompat.getColor(context, R.color.primary_color))
        }
    }
}

@BindingAdapter("app:eventProgressStatus")
fun TextView.setEventProgressStatus(
    progressStatus: EventProgressStatus? = null,
) {
    if (progressStatus == null) return

    when (progressStatus) {
        EventProgressStatus.Ended -> {
            text = context.getString(R.string.all_ended)
            setTextColor(ContextCompat.getColor(context, R.color.gray))
        }

        EventProgressStatus.InProgress -> {
            text = context.getString(R.string.all_in_progress)
            setTextColor(ContextCompat.getColor(context, R.color.primary_color))
        }

        is EventProgressStatus.UpComing -> {
            text = context.getString(R.string.all_up_coming, progressStatus.remainingDays)
            setTextColor(ContextCompat.getColor(context, R.color.primary_color))
        }
    }
}

@BindingAdapter("app:onOfflineMode")
fun TextView.setOnOfflineMode(
    onOfflineMode: OnOfflineMode? = null,
) {
    if (onOfflineMode == null) return

    text = when (onOfflineMode) {
        OnOfflineMode.OFFLINE -> context.getString(R.string.all_offline)
        OnOfflineMode.ONLINE -> context.getString(R.string.all_online)
        OnOfflineMode.ON_OFFLINE -> context.getString(R.string.all_onoffline)
    }
}
