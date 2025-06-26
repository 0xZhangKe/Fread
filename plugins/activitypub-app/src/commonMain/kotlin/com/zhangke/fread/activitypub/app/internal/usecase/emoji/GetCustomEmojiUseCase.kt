package com.zhangke.fread.activitypub.app.internal.usecase.emoji

import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.screen.status.post.adapter.CustomEmojiAdapter
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.GroupedCustomEmojiCell
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

class GetCustomEmojiUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    private val emojiAdapter: CustomEmojiAdapter,
) {

    suspend operator fun invoke(locator: PlatformLocator): Result<List<GroupedCustomEmojiCell>> {
        return clientManager.getClient(locator)
            .emojiRepo
            .getCustomEmojis()
            .map { list -> list.map(emojiEntityAdapter::toCustomEmoji) }
            .map { emojiAdapter.toEmojiCell(it) }
    }
}
