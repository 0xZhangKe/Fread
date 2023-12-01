package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubPlatformUri
import com.zhangke.utopia.status.emoji.CustomEmoji
import com.zhangke.utopia.status.emoji.ICustomEmojiProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class ActivityPubCustomEmojiProvider @Inject constructor(
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) : ICustomEmojiProvider {

    override suspend fun getCustomEmojiList(platform: BlogPlatform): Result<List<CustomEmoji>>? {
        if (platform.protocol != ACTIVITY_PUB_PROTOCOL) return null
        val uri = ActivityPubPlatformUri.parse(platform.uri)!!
        val client = obtainActivityPubClient(uri.serverHost)
        return client.emojiRepo.getCustomEmojis().map { list ->
            list.map(emojiEntityAdapter::toCustomEmoji)
        }
    }
}
