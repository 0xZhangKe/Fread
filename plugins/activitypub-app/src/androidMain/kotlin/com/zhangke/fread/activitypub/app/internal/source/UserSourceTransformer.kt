package com.zhangke.fread.activitypub.app.internal.source

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.activitypub.app.internal.usecase.emoji.MapCustomEmojiUseCase
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.status.source.StatusSource
import me.tatarka.inject.annotations.Inject

class UserSourceTransformer @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val mapCustomEmoji: MapCustomEmojiUseCase,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    private val context: ApplicationContext,
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
