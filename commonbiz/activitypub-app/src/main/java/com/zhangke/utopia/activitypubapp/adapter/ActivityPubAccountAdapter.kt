package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.utopia.activitypubapp.account.repo.ActivityPubLoggedAccountEntity
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.source.user.UserSourceEntry
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import javax.inject.Inject

class ActivityPubAccountAdapter @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
) {

    fun createDBEntity(
        instance: ActivityPubInstanceEntity,
        account: ActivityPubAccountEntity,
        token: ActivityPubTokenEntity,
        active: Boolean,
    ): ActivityPubLoggedAccountEntity {
        return ActivityPubLoggedAccountEntity(
            uri = accountToUriUseCase.adapt(account).toString(),
            id = accountToWebFinger(account).toString(),
            platform = instanceAdapter.createPlatform(instance),
            host = ActivityPubUrl.create(instance.domain)!!.host,
            name = account.displayName,
            description = account.note,
            avatar = account.avatar,
            homepage = account.url,
            active = active,
            token = token,
        )
    }

    fun fromUserSourceEntity(entity: UserSourceEntry): UserSource {
        return UserSource(
            name = entity.nickName,
            webFinger = entity.webFinger,
            description = entity.description,
            thumbnail = entity.thumbnail,
            userId = entity.userId,
        )
    }

    fun toSourceEntityEntry(source: UserSource): UserSourceEntry {
        return UserSourceEntry(
            nickName = source.name,
            webFinger = source.webFinger,
            description = source.description,
            thumbnail = source.thumbnail,
            userId = source.userId,
        )
    }
}
