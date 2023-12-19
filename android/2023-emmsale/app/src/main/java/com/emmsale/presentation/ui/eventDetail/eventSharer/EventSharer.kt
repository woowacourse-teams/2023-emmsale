package com.emmsale.presentation.ui.eventDetail.eventSharer

import android.content.Context
import com.emmsale.R
import com.emmsale.presentation.common.extension.showToast
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.FeedTemplate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class EventSharer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val isNotKakaoTalkInstalled
        get() = !ShareClient.instance.isKakaoTalkSharingAvailable(context)

    fun shareEvent(eventTemplate: FeedTemplate) {
        if (isNotKakaoTalkInstalled) {
            handleKakaoNotInstalledError(eventTemplate)
            return
        }

        share(eventTemplate)
    }

    private fun handleKakaoNotInstalledError(eventTemplate: FeedTemplate) {
        val sharerUrl = WebSharerClient.instance.makeDefaultUrl(eventTemplate)

        runCatching {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
        }.onFailure {
            context.showToast(R.string.eventdetail_no_exist_browser)
        }

        runCatching {
            KakaoCustomTabsClient.open(context, sharerUrl)
        }.onFailure {
            context.showToast(R.string.eventdetail_no_exist_browser)
        }
    }

    private fun share(eventTemplate: FeedTemplate) {
        ShareClient.instance.shareDefault(context, eventTemplate) { sharingResult, error ->
            if (error != null) {
                context.showToast(R.string.eventdetail_kakao_share_fail)
            } else if (sharingResult != null) {
                context.startActivity(sharingResult.intent)
            }
        }
    }
}
