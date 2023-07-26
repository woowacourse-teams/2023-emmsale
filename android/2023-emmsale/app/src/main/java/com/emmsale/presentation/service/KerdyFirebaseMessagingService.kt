package com.emmsale.presentation.service

import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class KerdyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Toast.makeText(this, "토큰 도착!", Toast.LENGTH_SHORT).show()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // FcmTokenRepository에 저장
    }
}
