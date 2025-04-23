package com.zhangke.fread.activitypub.app.internal.push

import com.zhangke.fread.common.push.PushMessage

actual class ActivityPubPushMessageReceiverHelper {

    actual fun onReceiveNewMessage(message: PushMessage) {
        error("Not implemented")
    }
}
