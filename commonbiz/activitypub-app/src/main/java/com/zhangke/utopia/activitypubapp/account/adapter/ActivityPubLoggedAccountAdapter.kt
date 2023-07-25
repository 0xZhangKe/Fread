package com.zhangke.utopia.activitypubapp.account.adapter

import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.utopia.activitypubapp.account.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypubapp.account.repo.ActivityPubLoggedAccountEntity
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypubapp.model.ActivityPubUser
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.user.LoggedAccount
import javax.inject.Inject

class ActivityPubLoggedAccountAdapter @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
) {

    fun fromLoggedAccount(
        account: LoggedAccount,
        token: ActivityPubTokenEntity,
    ): ActivityPubLoggedAccount {
        return ActivityPubLoggedAccount(
            userId = account.userId,
            uri = account.uri,
            platform = account.platform,
            host = account.host,
            name = account.name,
            description = account.description,
            avatar = account.avatar,
            homepage = account.homepage,
            active = account.active,
            validate = account.validate,
            token = token,
        )
    }

    fun toLoggedAccount(account: ActivityPubLoggedAccount): LoggedAccount {
        return LoggedAccount(
            id = account.id,
            platform = account.platform,
            host = account.host,
            name = account.name,
            description = account.description,
            avatar = account.avatar,
            homepage = account.homepage,
            active = account.active,
            validate = account.validate,
        )
    }

    fun adapt(
        entity: ActivityPubLoggedAccountEntity,
        validate: Boolean
    ): ActivityPubLoggedAccount {
        return ActivityPubLoggedAccount(
            userId = entity.userId,
            uri = entity.uri,
            webFinger = entity.webFinger,
            platform = entity.platform,
            host = entity.host,
            name = entity.name,
            description = entity.description,
            avatar = entity.avatar,
            homepage = entity.homepage,
            active = entity.active,
            validate = validate,
            token = entity.token,
        )
    }

    fun recovery(
        user: ActivityPubLoggedAccount,
    ): ActivityPubLoggedAccountEntity {
        return ActivityPubLoggedAccountEntity(
            userId = user.userId,
            uri = user.uri,
            webFinger = user.webFinger,
            platform = user.platform,
            host = user.host,
            name = user.name,
            description = user.description,
            avatar = user.avatar,
            homepage = user.homepage,
            active = user.active,
            token = user.token,
        )
    }

    fun createFromAccount(
        instance: ActivityPubInstanceEntity,
        user: ActivityPubUser,
        token: ActivityPubTokenEntity,
        active: Boolean,
    ): ActivityPubLoggedAccount {
        return ActivityPubLoggedAccount(
            userId = user.id,
            uri = user.uri.toString(),
            webFinger = user.webFinger,
            platform = instanceAdapter.createPlatform(instance),
            host = ActivityPubUrl.create(instance.domain)!!.host,
            name = user.displayName,
            description = user.note,
            avatar = user.avatar,
            homepage = user.homePageUrl,
            active = active,
            token = token,
            validate = active,
        )
    }
}
