package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.activitypub.entry.ActivityPubToken
import com.zhangke.utopia.activitypubapp.domain.ActivityPubAccountToWebFingerUseCase
import com.zhangke.utopia.activitypubapp.protocol.buildUserSourceUri
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.source.user.UserSourceEntry
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserEntity
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.user.UtopiaUser
import javax.inject.Inject

class ActivityPubAccountAdapter @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
    private val accountToWebFinger: ActivityPubAccountToWebFingerUseCase,
) {

    fun createSource(account: ActivityPubAccount): UserSource {
        return UserSource(
            userId = account.id,
            name = account.displayName,
            description = account.note,
            thumbnail = account.avatar,
            webFinger = accountToWebFinger(account),
            uri = account.generateUri(),
        )
    }

    fun createEntity(
        instance: ActivityPubInstance,
        account: ActivityPubAccount,
        token: ActivityPubToken,
        active: Boolean,
    ): ActivityPubUserEntity {
        return ActivityPubUserEntity(
            uri = account.generateUri(),
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

    private fun ActivityPubAccount.generateUri(): String {
        return buildUserSourceUri(accountToWebFinger(this), id).toString()
    }

    fun toEntity(
        user: UtopiaUser,
        uri: String,
        webFinger: WebFinger,
        token: ActivityPubToken,
    ): ActivityPubUserEntity {
        return ActivityPubUserEntity(
            uri = uri,
            id = webFinger.toString(),
            platform = user.platform,
            host = user.host,
            name = user.name,
            description = user.description,
            avatar = user.avatar,
            homepage = user.homepage,
            active = user.active,
            token = token,
        )
    }

    fun toUtopiaUser(
        entity: ActivityPubUserEntity,
        validate: Boolean
    ): UtopiaUser {
        return UtopiaUser(
            id = entity.id,
            platform = entity.platform,
            host = entity.host,
            name = entity.name,
            description = entity.description,
            avatar = entity.avatar,
            homepage = entity.homepage,
            active = entity.active,
            validate = validate,
        )
    }

    fun fromUserSourceEntity(entity: UserSourceEntry): UserSource {
        return UserSource(
            uri = entity.webFinger.toString(),
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
