package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.utopia.activitypubapp.model.ActivityPubUser
import com.zhangke.utopia.activitypubapp.usecase.ActivityPubAccountToUriUseCase
import javax.inject.Inject

class ActivityPubUserAdapter @Inject constructor(
    private val accountToUriUseCase: ActivityPubAccountToUriUseCase,
) {

    fun createUser(entity: ActivityPubAccountEntity): ActivityPubUser {
        return ActivityPubUser(
            uri = accountToUriUseCase.adapt(entity),
            username = entity.username,
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
}
