package com.zhangke.fread.activitypub.app.internal.push

import com.zhangke.fread.common.push.PushMessage
import com.zhangke.fread.common.push.PushMessageReceiver
import com.zhangke.krouter.annotation.Service

expect class ActivityPubPushMessageReceiverHelper() {

    fun onReceiveNewMessage(message: PushMessage)
}

@Service
class ActivityPubPushMessageReceiver : PushMessageReceiver {

    private val pushMessageReceiverHelper = ActivityPubPushMessageReceiverHelper()

    override fun onReceiveNewMessage(message: PushMessage) {
        pushMessageReceiverHelper.onReceiveNewMessage(message)
    }
}
