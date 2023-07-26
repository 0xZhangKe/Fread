package com.zhangke.utopia.activitypubapp.user

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.utopia.activitypubapp.model.ActivityPubUser
import com.zhangke.utopia.activitypubapp.uri.user.ActivityPubUserUri
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

class ActivityPubUserAdapter @Inject constructor() {

    fun adapt(entity: ActivityPubAccountEntity): ActivityPubUser {
        val webFinger = accountToWebFinger(entity)
        return ActivityPubUser(
            id = entity.id,
            webFinger = webFinger,
            uri = ActivityPubUserUri.create(entity.id, webFinger).toString(),
            userName = entity.username,
            displayName = entity.displayName,
            locked = entity.locked,
            bot = entity.bot,
            discoverable = entity.discoverable,
            group = entity.group,
            createdAt = entity.createdAt,
            note = entity.note,
            homePageUrl = entity.url,
            avatar = entity.avatar,
            avatarStatic = entity.avatarStatic,
            header = entity.header,
            headerStatic = entity.headerStatic,
            followersCount = entity.followersCount,
            followingCount = entity.followingCount,
            statusesCount = entity.statusesCount,
            lastStatusAt = entity.lastStatusAt,
        )
    }

    private fun accountToWebFinger(account: ActivityPubAccountEntity): WebFinger {
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
