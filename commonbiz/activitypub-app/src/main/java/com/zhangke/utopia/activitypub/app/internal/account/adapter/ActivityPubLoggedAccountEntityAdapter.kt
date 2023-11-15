package com.zhangke.utopia.activitypub.app.internal.account.adapter

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.account.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggedAccountEntity
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.uri.user.ActivityPubUserUri
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubUrl
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubLoggedAccountEntityAdapter @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
) {

    fun toAccount(
        entity: ActivityPubLoggedAccountEntity,
    ): ActivityPubLoggedAccount {
        return ActivityPubLoggedAccount(
            userId = entity.userId,
            uri = StatusProviderUri.create(entity.uri)!!,
            webFinger = entity.webFinger,
            platform = entity.platform,
            host = entity.host,
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
            platform = user.platform,
            host = user.host,
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
            uri = ActivityPubUserUri.create(account.id, webFinger).toStatusProviderUri(),
            webFinger = webFinger,
            platform = instanceAdapter.toPlatform(instance),
            host = ActivityPubUrl.create(instance.domain)!!.host,
            name = account.displayName,
            description = account.note,
            avatar = account.avatar,
            homepage = account.url,
            active = active,
            token = token,
        )
    }

    private fun accountToWebFinger(account: ActivityPubAccountEntity): WebFinger {
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
