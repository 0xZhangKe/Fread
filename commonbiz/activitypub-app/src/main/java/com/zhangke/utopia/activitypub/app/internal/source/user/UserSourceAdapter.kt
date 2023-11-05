package com.zhangke.utopia.activitypub.app.internal.source.user

import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubUser
import javax.inject.Inject

class UserSourceAdapter @Inject constructor() {

    fun adapt(user: ActivityPubUser): UserSource {
        return UserSource(
            userId = user.id,
            webFinger = user.webFinger,
            name = user.displayName,
            description = user.note,
            thumbnail = user.avatar,
        )
    }
}
