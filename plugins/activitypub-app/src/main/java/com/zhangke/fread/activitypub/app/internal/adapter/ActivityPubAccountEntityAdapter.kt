package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.analytics.report
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class ActivityPubAccountEntityAdapter @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) {

    fun toAuthor(
        entity: ActivityPubAccountEntity,
    ): BlogAuthor {
        val webFinger = toWebFinger(entity)
        return BlogAuthor(
            uri = toUri(entity),
            webFinger = webFinger,
            name = entity.displayName,
            description = entity.note,
            avatar = entity.avatar,
            emojis = entity.emojis.map(emojiEntityAdapter::toEmoji),
        )
    }

    fun toUri(entity: ActivityPubAccountEntity): FormalUri {
        val webFinger = toWebFinger(entity)
        return userUriTransformer.build(webFinger, FormalBaseUrl.parse(entity.url)!!)
    }

    fun toWebFinger(account: ActivityPubAccountEntity): WebFinger {
        try {
            WebFinger.create(account.acct)?.let { return it }
            WebFinger.create(account.url)!!.let { return it }
        } catch (e: Throwable) {
            report("ToWebFingerException") {
                putString("acct", account.acct)
                putString("url", account.url)
                putString("displayName", account.displayName)
            }
            throw e
        }
    }
}
