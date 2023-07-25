package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.model.ActivityPubUser
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
