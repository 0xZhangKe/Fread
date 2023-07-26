package com.zhangke.utopia.activitypubapp.account.adapter

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.utopia.activitypubapp.account.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypubapp.account.repo.ActivityPubLoggedAccountEntity
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypubapp.uri.user.ActivityPubUserUri
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

class ActivityPubLoggedAccountAdapter @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
) {

    fun adapt(
        entity: ActivityPubLoggedAccountEntity,
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
        account: ActivityPubAccountEntity,
        token: ActivityPubTokenEntity,
        active: Boolean,
    ): ActivityPubLoggedAccount {
        val webFinger = accountToWebFinger(account)
        return ActivityPubLoggedAccount(
            userId = account.id,
            uri = ActivityPubUserUri.create(account.id, webFinger).toString(),
            webFinger = webFinger,
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

    private fun accountToWebFinger(account: ActivityPubAccountEntity): WebFinger {
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
