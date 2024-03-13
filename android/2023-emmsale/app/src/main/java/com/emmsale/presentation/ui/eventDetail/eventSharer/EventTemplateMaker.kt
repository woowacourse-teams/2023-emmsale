package com.emmsale.presentation.ui.eventDetail.eventSharer

import android.content.Context
import com.emmsale.R
import com.emmsale.presentation.ui.eventDetail.EventDetailViewModel
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class EventTemplateMaker @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun create(
        eventId: Long,
        eventName: String,
        posterUrl: String,
    ): FeedTemplate {
        val shareLink = Link(
            androidExecutionParams = mapOf(EventDetailViewModel.EVENT_ID_KEY to eventId.toString()),
            iosExecutionParams = mapOf(EventDetailViewModel.EVENT_ID_KEY to eventId.toString())
        )

        return FeedTemplate(
            content = Content(
                title = eventName,
                description = context.getString(R.string.eventdetail_share_template_description),
                imageUrl = posterUrl,
                link = shareLink,
            ),
            buttons = listOf(
                Button(
                    title = context.getString(R.string.eventdetail_share_button_title),
                    link = shareLink,
                ),
            ),
        )
    }
}
