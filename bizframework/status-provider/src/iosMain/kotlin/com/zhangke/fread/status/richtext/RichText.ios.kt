package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention

internal actual fun RichText.parse(
    mentions: List<Mention>,
    hashTags: List<HashtagInStatus>,
    emojis: List<Emoji>,
    parsePossibleHashtag: Boolean,
): CharSequence {
    TODO("Not yet implemented")
}