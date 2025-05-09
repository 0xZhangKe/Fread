package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountEntity
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.analytics.reportToLogger
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class ActivityPubLoggedAccountAdapter @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
    private val userUriTransformer: UserUriTransformer,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) {

    suspend fun adapt(
        entity: ActivityPubLoggedAccountEntity,
    ): ActivityPubLoggedAccount {
        return ActivityPubLoggedAccount(
            userId = entity.userId,
            uri = FormalUri.from(entity.uri)!!,
            webFinger = entity.webFinger,
            platform = entity.platform.toPlatform(),
            baseUrl = entity.baseUrl,
            userName = entity.name,
            description = entity.description,
            avatar = entity.avatar,
            url = entity.url,
            token = entity.token,
            emojis = entity.emojis,
        )
    }

    fun recovery(
        user: ActivityPubLoggedAccount,
        addedTimestamp: Long,
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
            url = user.url,
            token = user.token,
            emojis = user.emojis,
            addedTimestamp = addedTimestamp,
        )
    }

    suspend fun createFromAccount(
        baseUrl: FormalBaseUrl,
        instance: ActivityPubInstanceEntity,
        account: ActivityPubAccountEntity,
        token: ActivityPubTokenEntity,
    ): ActivityPubLoggedAccount {
        return createFromAccount(
            platform = instanceAdapter.toPlatform(baseUrl, instance),
            account = account,
            token = token,
        )
    }

    fun createFromAccount(
        platform: BlogPlatform,
        account: ActivityPubAccountEntity,
        token: ActivityPubTokenEntity,
    ): ActivityPubLoggedAccount {
        val webFinger = accountToWebFinger(account, platform.baseUrl)
        return ActivityPubLoggedAccount(
            userId = account.id,
            uri = userUriTransformer.build(webFinger, platform.baseUrl),
            webFinger = webFinger,
            platform = platform,
            baseUrl = platform.baseUrl,
            userName = account.displayName,
            description = account.note,
            avatar = account.avatar,
            url = account.url,
            token = token,
            emojis = account.emojis.map(emojiEntityAdapter::toEmoji),
        )
    }

    private suspend fun ActivityPubLoggedAccountEntity.BlogPlatformEntity.toPlatform(): BlogPlatform =
        BlogPlatform(
            uri = uri,
            name = name,
            description = description,
            baseUrl = baseUrl,
            thumbnail = thumbnail,
            protocol = createActivityPubProtocol(),
        )

    private fun BlogPlatform.toEntity() = ActivityPubLoggedAccountEntity.BlogPlatformEntity(
        uri = uri,
        name = name,
        description = description,
        baseUrl = baseUrl,
        thumbnail = thumbnail,
    )

    private fun accountToWebFinger(
        account: ActivityPubAccountEntity,
        baseUrl: FormalBaseUrl,
    ): WebFinger {
        try {
            WebFinger.create(account.acct, baseUrl)?.let { return it }
            WebFinger.create(account.url)!!.let { return it }
        } catch (e: Throwable) {
            e.printStackTrace()
            reportToLogger("WebFingerCreateError") {
                put("acct", account.acct)
                put("url", account.url)
            }
            throw e
        }
    }
}
