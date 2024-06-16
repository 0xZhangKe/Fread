package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.android.HtmlParser

class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val mentions: List<Mention>,
    private val hashTags: List<HashtagInStatus>,
    private val emojis: List<Emoji>,
): java.io.Serializable {

    @Transient
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
