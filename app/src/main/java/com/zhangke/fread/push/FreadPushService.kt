package com.zhangke.fread.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FreadPushService : FirebaseMessagingService() {

    init {
        Log.d("F_TEST", "init")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("F_TEST", "onCreate")
    }

    override fun onTimeout(startId: Int) {
        super.onTimeout(startId)
        Log.d("F_TEST", "onTimeout: $startId")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("F_TEST", "onNewToken: $token")
    }

    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
        Log.d("F_TEST", "onSendError: $msgId, $exception")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("F_TEST", "onMessageReceived: $message")
        message.data.entries.forEach {
            Log.d("F_TEST", "onMessageReceived: ${it.key}: ${it.value}")
        }
    }
}
