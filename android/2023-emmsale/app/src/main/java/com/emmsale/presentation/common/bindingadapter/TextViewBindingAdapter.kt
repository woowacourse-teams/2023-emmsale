package com.emmsale.presentation.common.bindingadapter

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.data.model.EventApplyingStatus
import com.emmsale.data.model.EventProgressStatus
import com.emmsale.data.model.OnOfflineMode
import com.emmsale.data.model.PaymentType

private const val D_DAY = 0

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

@BindingAdapter("app:eventApplyingStatus")
fun TextView.setEventApplyingStatus(
    applicationStatus: EventApplyingStatus? = null,
) {
    if (applicationStatus == null) return

    when (applicationStatus) {
        EventApplyingStatus.Ended -> {
            text = context.getString(R.string.all_applying_ended)
            setTextColor(ContextCompat.getColor(context, R.color.gray))
        }

        is EventApplyingStatus.InProgress -> {
            text = if (applicationStatus.daysUntilDeadline == D_DAY) {
                context.getString(R.string.all_applying_in_progress_d_day)
            } else {
                context.getString(
                    R.string.all_applying_in_progress,
                    applicationStatus.daysUntilDeadline,
                )
            }
            setTextColor(ContextCompat.getColor(context, R.color.primary_color))
        }

        is EventApplyingStatus.UpComing -> {
            text = if (applicationStatus.daysUntilStart == D_DAY) {
                context.getString(R.string.all_applying_up_coming_d_day)
            } else {
                context.getString(
                    R.string.all_applying_up_coming,
                    applicationStatus.daysUntilStart,
                )
            }
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
            text = if (progressStatus.daysUntilStart == D_DAY) {
                context.getString(R.string.all_event_up_coming_d_day)
            } else {
                context.getString(
                    R.string.all_event_up_coming,
                    progressStatus.daysUntilStart,
                )
            }

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
