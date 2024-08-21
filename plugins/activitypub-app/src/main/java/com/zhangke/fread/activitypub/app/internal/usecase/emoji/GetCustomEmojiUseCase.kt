package com.zhangke.fread.activitypub.app.internal.usecase.emoji

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.screen.status.post.adapter.CustomEmojiAdapter
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.GroupedCustomEmojiCell
import com.zhangke.fread.status.model.IdentityRole
import javax.inject.Inject

class GetCustomEmojiUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    private val emojiAdapter: CustomEmojiAdapter,
) {

    suspend operator fun invoke(baseUrl: FormalBaseUrl): Result<List<GroupedCustomEmojiCell>> {
        return clientManager.getClient(IdentityRole(null, baseUrl))
            .emojiRepo
            .getCustomEmojis()
            .map { list -> list.map(emojiEntityAdapter::toCustomEmoji) }
            .map { emojiAdapter.toEmojiCell(it) }
    }
}
