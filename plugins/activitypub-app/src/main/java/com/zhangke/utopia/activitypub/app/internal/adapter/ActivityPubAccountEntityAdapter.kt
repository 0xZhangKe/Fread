package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

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
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
