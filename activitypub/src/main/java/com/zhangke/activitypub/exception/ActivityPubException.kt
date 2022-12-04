package com.zhangke.activitypub.exception

import com.zhangke.activitypub.entry.ActivityPubErrorEntry

/**
 * Created by ZhangKe on 2022/12/3.
 */

sealed class ActivityPubHttpException(message: String?, e: Throwable?) :
    RuntimeException(message, e) {

    class ServerInternalException(errorEntry: ActivityPubErrorEntry?, errorMessage: String?) :
        ActivityPubHttpException(errorEntry?.error ?: errorMessage, null)

    class RequestIllegalException(errorEntry: ActivityPubErrorEntry?, errorMessage: String?) :
        ActivityPubHttpException(errorEntry?.error ?: errorMessage, null)
}