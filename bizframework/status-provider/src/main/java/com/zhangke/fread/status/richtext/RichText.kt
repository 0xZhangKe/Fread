package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.android.HtmlParser
import kotlinx.serialization.Serializable

@Serializable
class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val mentions: List<Mention>,
    private val hashTags: List<HashtagInStatus>,
    private val emojis: List<Emoji>,
    private val parsePossibleHashtag: Boolean = false,
) : java.io.Serializable {

    @Transient
    private var charSequence: CharSequence? = null

    fun parse(): CharSequence {
        charSequence?.let { return it }
        return HtmlParser.parse(
            document = document,
            emojis = emojis,
            mentions = mentions,
            hashTag = hashTags,
            parsePossibleHashtag = parsePossibleHashtag,
        ).also { charSequence = it }
    }

    companion object {

        val empty = buildRichText("")
    }
}
