package com.zhangke.utopia.activitypub.app.internal.usecase.emoji

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import javax.inject.Inject

class MapAccountEntityEmojiUseCase @Inject constructor(
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    private val mapCustomEmoji: MapCustomEmojiUseCase,
) {

    operator fun invoke(account: ActivityPubAccountEntity): ActivityPubAccountEntity {
        val emojis = account.emojis.map(emojiEntityAdapter::toEmoji)
        val newNote = mapCustomEmoji(account.note, emojis)
        val newFields = account.fields.map { field ->
            field.copy(value = mapCustomEmoji(field.value, emojis))
        }
        return account.copy(
            note = newNote,
            fields = newFields,
        )
    }
}
