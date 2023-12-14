package com.zhangke.utopia.activitypub.app.internal.usecase.uri

import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubUriValidateUseCase @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val timelineUriTransformer: TimelineUriTransformer,
) {

    operator fun invoke(uri: FormalUri): Boolean {
        if (userUriTransformer.parse(uri) != null) return true
        if (timelineUriTransformer.parse(uri) != null) return true
        return false
    }
}
