package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubLoggedAccountEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class ActivityPubLoggedAccountAdapter @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
    private val userUriTransformer: UserUriTransformer,
) {

    fun adapt(
        entity: ActivityPubLoggedAccountEntity,
    ): ActivityPubLoggedAccount {
        return ActivityPubLoggedAccount(
            userId = entity.userId,
            uri = userUriTransformer.build(entity.webFinger),
            webFinger = entity.webFinger,
            platform = entity.platform.toPlatform(),
            baseUrl = entity.baseUrl,
            name = entity.name,
            description = entity.description,
            avatar = entity.avatar,
            homepage = entity.homepage,
            active = entity.active,
            token = entity.token,
        )
    }

    fun recovery(
        user: ActivityPubLoggedAccount,
    ): ActivityPubLoggedAccountEntity {
        return ActivityPubLoggedAccountEntity(
            userId = user.userId,
            uri = user.uri.toString(),
            webFinger = user.webFinger,
            platform = user.platform.toEntity(),
            baseUrl = user.baseUrl,
            name = user.userName,
            description = user.description,
            avatar = user.avatar,
            homepage = user.homepage,
            active = user.active,
            token = user.token,
        )
    }

    fun createFromAccount(
        instance: ActivityPubInstanceEntity,
        account: ActivityPubAccountEntity,
        token: ActivityPubTokenEntity,
        active: Boolean,
    ): ActivityPubLoggedAccount {
        val webFinger = accountToWebFinger(account)
        return ActivityPubLoggedAccount(
            userId = account.id,
            uri = userUriTransformer.build(webFinger),
            webFinger = webFinger,
            platform = instanceAdapter.toPlatform(instance),
            baseUrl = instance.domain.toBaseUrl(),
            name = account.displayName,
            description = account.note,
            avatar = account.avatar,
            homepage = account.url,
            active = active,
            token = token,
        )
    }

    private fun ActivityPubLoggedAccountEntity.BlogPlatformEntity.toPlatform(): BlogPlatform =
        BlogPlatform(
            uri = uri,
            name = name,
            description = description,
            baseUrl = baseUrl,
            thumbnail = thumbnail,
            protocol = protocol,
        )

    private fun BlogPlatform.toEntity() = ActivityPubLoggedAccountEntity.BlogPlatformEntity(
        uri = uri,
        name = name,
        description = description,
        baseUrl = baseUrl,
        thumbnail = thumbnail,
        protocol = protocol,
    )

    fun accountToWebFinger(account: ActivityPubAccountEntity): WebFinger {
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
