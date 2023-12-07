package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubUserAuthor
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import javax.inject.Inject

class ActivityPubAccountEntityAdapter @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
) {

    fun toAuthor(
        entity: ActivityPubAccountEntity,
    ): ActivityPubUserAuthor {
        val webFinger = toWebFinger(entity)
        return ActivityPubUserAuthor(
            uri = userUriTransformer.build(entity.id, webFinger),
            webFinger = webFinger,
            name = entity.displayName,
            description = entity.note,
            avatar = entity.avatarStatic,
        )
    }

    fun toWebFinger(account: ActivityPubAccountEntity): WebFinger {
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
