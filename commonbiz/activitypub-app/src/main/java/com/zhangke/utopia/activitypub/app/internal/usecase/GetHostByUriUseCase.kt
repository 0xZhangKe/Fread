package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubUrl
import com.zhangke.framework.utils.WebFinger
import javax.inject.Inject

class GetHostByUriUseCase @Inject constructor() {

    operator fun invoke(uri: String): String? {
        WebFinger.create(uri)?.let { return it.host }
        ActivityPubUrl.create(uri)?.let { return it.host }
        return null
    }
}
