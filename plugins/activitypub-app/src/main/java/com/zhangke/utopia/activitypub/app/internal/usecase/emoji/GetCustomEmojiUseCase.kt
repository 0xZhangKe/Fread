package com.zhangke.utopia.activitypub.app.internal.usecase.emoji

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypub.app.internal.model.CustomEmoji
import javax.inject.Inject

class GetCustomEmojiUseCase @Inject constructor(
    private val obtainActivityPubClient: ObtainActivityPubClientUseCase,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
) {

    suspend operator fun invoke(serverHost: String): Result<List<CustomEmoji>> {
        return obtainActivityPubClient(serverHost).emojiRepo
            .getCustomEmojis().map { list ->
                list.map(emojiEntityAdapter::toCustomEmoji)
            }
    }
}
