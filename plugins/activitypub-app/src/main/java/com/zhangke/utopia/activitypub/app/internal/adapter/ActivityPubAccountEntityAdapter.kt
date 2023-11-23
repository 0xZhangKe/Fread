package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubUserAuthor
import com.zhangke.utopia.activitypub.app.internal.source.user.UserSource
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import javax.inject.Inject

class ActivityPubAccountEntityAdapter @Inject constructor() {

    fun toAuthor(
        entity: ActivityPubAccountEntity,
    ): ActivityPubUserAuthor {
        val webFinger = toWebFinger(entity)
        return ActivityPubUserAuthor(
            uri = ActivityPubUserUri.create(entity.id, webFinger),
            webFinger = webFinger,
            name = entity.displayName,
            description = entity.note,
            avatar = entity.avatarStatic,
        )
    }

    fun toUserSource(entity: ActivityPubAccountEntity): UserSource {
        return UserSource(
            userId = entity.id,
            webFinger = toWebFinger(entity),
            name = entity.displayName,
            description = entity.note,
            thumbnail = entity.avatar,
        )
    }

    fun toWebFinger(account: ActivityPubAccountEntity): WebFinger {
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
