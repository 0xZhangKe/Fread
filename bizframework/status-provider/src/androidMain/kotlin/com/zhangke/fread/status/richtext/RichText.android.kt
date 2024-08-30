package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.android.HtmlParser

internal actual fun RichText.parse(
    mentions: List<Mention>,
    hashTags: List<HashtagInStatus>,
    emojis: List<Emoji>,
    parsePossibleHashtag: Boolean,
): CharSequence {
    return HtmlParser.parse(
        document = document,
        emojis = emojis,
        mentions = mentions,
        hashTag = hashTags,
        parsePossibleHashtag = parsePossibleHashtag,
    )
}
