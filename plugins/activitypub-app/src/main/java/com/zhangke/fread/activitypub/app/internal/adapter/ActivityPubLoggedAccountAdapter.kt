package com.zhangke.fread.activitypub.app.internal.adapter

import android.content.Context
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountEntity
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.platform.BlogPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ActivityPubLoggedAccountAdapter @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
    private val userUriTransformer: UserUriTransformer,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    @ApplicationContext private val context: Context,
) {

    fun adapt(
        entity: ActivityPubLoggedAccountEntity,
    ): ActivityPubLoggedAccount {
        return ActivityPubLoggedAccount(
            userId = entity.userId,
            uri = userUriTransformer.build(entity.webFinger, FormalBaseUrl.parse(entity.url)!!),
            webFinger = entity.webFinger,
            platform = entity.platform.toPlatform(),
            baseUrl = entity.baseUrl,
            name = entity.name,
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

    fun createFromAccount(
        instance: ActivityPubInstanceEntity,
        account: ActivityPubAccountEntity,
        token: ActivityPubTokenEntity,
    ): ActivityPubLoggedAccount {
        val baseUrl = FormalBaseUrl.parse(account.url)!!
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
        val webFinger = accountToWebFinger(account)
        val baseUrl = FormalBaseUrl.parse(account.url)!!
        return ActivityPubLoggedAccount(
            userId = account.id,
            uri = userUriTransformer.build(webFinger, baseUrl),
            webFinger = webFinger,
            platform = platform,
            baseUrl = baseUrl,
            name = account.displayName,
            description = account.note,
            avatar = account.avatar,
            url = account.url,
            token = token,
            emojis = account.emojis.map(emojiEntityAdapter::toEmoji),
        )
    }

    private fun ActivityPubLoggedAccountEntity.BlogPlatformEntity.toPlatform(): BlogPlatform =
        BlogPlatform(
            uri = uri,
            name = name,
            description = description,
            baseUrl = baseUrl,
            thumbnail = thumbnail,
            protocol = createActivityPubProtocol(context),
        )

    private fun BlogPlatform.toEntity() = ActivityPubLoggedAccountEntity.BlogPlatformEntity(
        uri = uri,
        name = name,
        description = description,
        baseUrl = baseUrl,
        thumbnail = thumbnail,
    )

    fun accountToWebFinger(account: ActivityPubAccountEntity): WebFinger {
        try {
            WebFinger.create(account.acct)?.let { return it }
            WebFinger.create(account.url)!!.let { return it }
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }
}
