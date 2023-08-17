package com.emmsale.presentation.common.firebase.analytics

import com.emmsale.presentation.KerdyApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.ParametersBuilder
import com.google.firebase.analytics.ktx.logEvent

/**
 * ANALYTICS EVENT NAMES
 *
 * */
private const val EVENT_CLICK = "event_click"
private const val COMMENT = "button_click"
private const val RECRUITMENT = "recruitment"
private const val WRITE_RECRUITMENT = "write_recruitment"
private const val INTEREST_TAGS = "interest_tags"
private const val CHANGE_CONFIG = "change_config"

/**
 *  ANALYTICS PARAM KEYS
 *
 *  */
private const val EVENT_NAME = "event_name"
private const val EVENT_ID = "event_id"
private const val USER_ID = "user_id"
private const val INTEREST_TAG_TYPE = "tag_type"
private const val CONFIG_TYPE = "config_type"
private const val WRITING_TYPE = "writing_type"
private const val WRITING_CONTENT = "writing_content"

fun log(event: String, parameters: ParametersBuilder.() -> Unit = {}) {
    KerdyApplication.firebaseAnalytics.logEvent(event, parameters)
}

fun logScreen(screenName: String) {
    log(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
    }
}

fun logEventClick(eventName: String, eventId: Long, memberId: Long) {
    log(EVENT_CLICK) {
        param(EVENT_NAME, eventName)
        param(EVENT_ID, eventId)
        param(USER_ID, memberId)
    }
}

fun logComment(commentName: String, commentId: Long, memberId: Long) {
    log(COMMENT) {
        param(EVENT_NAME, commentName)
        param(EVENT_ID, commentId)
        param(USER_ID, memberId)
    }
}

fun logWriting(writingType: String, writingContent: String, eventId: Long) {
    log(WRITE_RECRUITMENT) {
        param(WRITING_TYPE, writingType)
        param(WRITING_CONTENT, writingContent)
        param(EVENT_ID, eventId)
    }
}

fun logRecruitment(recruitmentMessage: String, memberId: Long) {
    log(RECRUITMENT) {
        param(WRITING_CONTENT, recruitmentMessage)
        param(USER_ID, memberId)
    }
}

fun logInterestTags(tags: List<String>) {
    tags.forEach { tag ->
        log(INTEREST_TAGS) {
            param(INTEREST_TAG_TYPE, tag)
        }
    }
}

fun logChangeConfig(configName: String, configState: Boolean) {
    log(CHANGE_CONFIG) {
        param(CONFIG_TYPE, configName)
    }
}
