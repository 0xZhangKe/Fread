package com.zhangke.activitypub.exception

import com.zhangke.activitypub.entry.ActivityPubErrorEntry

/**
 * Created by ZhangKe on 2022/12/3.
 */

sealed class ActivityPubHttpException(message: String?, e: Throwable?) :
    RuntimeException(message, e) {

    class ServerInternalException(val errorEntry: ActivityPubErrorEntry?, errorMessage: String?) :
        ActivityPubHttpException(errorEntry?.error ?: errorMessage, null)

    class RequestIllegalException(val errorEntry: ActivityPubErrorEntry?, errorMessage: String?) :
        ActivityPubHttpException(errorEntry?.error ?: errorMessage, null)

    class UnauthorizedException(val errorEntry: ActivityPubErrorEntry?, errorMessage: String?) :
        ActivityPubHttpException(errorEntry?.error ?: errorMessage, null)
}