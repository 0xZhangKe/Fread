package com.zhangke.utopia.activitypub.app.internal.usecase.emoji

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.CustomEmoji
import javax.inject.Inject

class GetCustomEmojiUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) {

    suspend operator fun invoke(baseUrl: FormalBaseUrl): Result<List<CustomEmoji>> {
        return clientManager.getClient(baseUrl).emojiRepo
            .getCustomEmojis().map { list ->
                list.map(emojiEntityAdapter::toCustomEmoji)
            }
    }
}
