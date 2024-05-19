package com.zhangke.utopia.activitypub.app.internal.source

import android.content.Context
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.createActivityPubProtocol
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.emoji.MapCustomEmojiUseCase
import com.zhangke.utopia.status.source.StatusSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserSourceTransformer @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val mapCustomEmoji: MapCustomEmojiUseCase,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    @ApplicationContext private val context: Context,
) {

    fun createByUserEntity(entity: ActivityPubAccountEntity): StatusSource {
        val webFinger = accountEntityAdapter.toWebFinger(entity)
        val uri = userUriTransformer.build(webFinger, FormalBaseUrl.parse(entity.url)!!)
        val emojis = entity.emojis.map(emojiEntityAdapter::toEmoji)
        return StatusSource(
            uri = uri,
            name = entity.displayName,
            description = mapCustomEmoji(entity.note, emojis),
            thumbnail = entity.avatar,
            protocol = createActivityPubProtocol(context),
        )
    }
}
