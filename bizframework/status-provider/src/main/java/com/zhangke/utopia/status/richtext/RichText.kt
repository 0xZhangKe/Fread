package com.zhangke.utopia.status.richtext

import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.richtext.android.HtmlParser

class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val mentions: List<Mention>,
    private val hashTags: List<HashtagInStatus>,
    private val emojis: List<Emoji>,
) {

    private var charSequence: CharSequence? = null

    fun parse(): CharSequence {
        charSequence?.let { return it }
        return HtmlParser.parse(
            document = document,
            emojis = emojis,
            mentions = mentions,
            hashTag = hashTags,
        ).also { charSequence = it }
    }
}
