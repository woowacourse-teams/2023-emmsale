package com.emmsale.presentation.common.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.data.model.EventStatus
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

@BindingAdapter("app:eventStatus")
fun TextView.setEventStatus(
    status: EventStatus? = null,
) {
    if (status == null) return

    text = when (status) {
        EventStatus.InProgress -> context.getString(R.string.all_in_progress)
        is EventStatus.Upcoming -> context.getString(R.string.all_upcoming, status.remainingDays)
        EventStatus.Ended -> context.getString(R.string.all_ended)
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

@BindingAdapter("app:eventTextColor")
fun TextView.setEventTextColor(
    eventStatus: EventStatus,
) {
    val color = when (eventStatus) {
        EventStatus.InProgress -> R.color.primary_color
        is EventStatus.Upcoming -> R.color.primary_color
        EventStatus.Ended -> R.color.gray
    }
    setTextColor(context.getColor(color))
}
