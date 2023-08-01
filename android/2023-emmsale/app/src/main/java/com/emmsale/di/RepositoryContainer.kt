package com.emmsale.di

import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.activity.ActivityRepositoryImpl
import com.emmsale.data.conference.ConferenceRepository
import com.emmsale.data.conference.ConferenceRepositoryImpl
import com.emmsale.data.fcmToken.FcmTokenRepository
import com.emmsale.data.fcmToken.FcmTokenRepositoryImpl
import com.emmsale.data.login.LoginRepository
import com.emmsale.data.login.LoginRepositoryImpl
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.member.MemberRepositoryImpl
import com.emmsale.data.token.TokenRepository
import com.emmsale.data.token.TokenRepositoryImpl

class RepositoryContainer(
    serviceContainer: ServiceContainer,
    preferenceContainer: SharedPreferenceContainer,
) {
    val loginRepository: LoginRepository by lazy {
        LoginRepositoryImpl(loginService = serviceContainer.loginService)
    }
    val tokenRepository: TokenRepository by lazy {
        TokenRepositoryImpl(preference = preferenceContainer.preference)
    }
    val activityRepository: ActivityRepository by lazy {
        ActivityRepositoryImpl(activityService = serviceContainer.activityService)
    }
    val memberRepository: MemberRepository by lazy {
        MemberRepositoryImpl(memberService = serviceContainer.memberService)
    }
    val conferenceRepository: ConferenceRepository by lazy {
        ConferenceRepositoryImpl(conferenceService = serviceContainer.conferenceService)
    }
    val fcmTokenRepository: FcmTokenRepository by lazy {
        FcmTokenRepositoryImpl(fcmTokenService = serviceContainer.fcmTokenService)
    }
}
