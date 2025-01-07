package com.zhangke.fread.status.richtext

import androidx.compose.ui.text.AnnotatedString
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.parser.HtmlParser
import kotlinx.serialization.Serializable

@Serializable
class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val mentions: List<Mention>,
    private val hashTags: List<HashtagInStatus>,
    private val emojis: List<Emoji>,
    private val parsePossibleHashtag: Boolean = false,
) : PlatformSerializable {
    fun parse(onLinkTargetClick: OnLinkTargetClick): AnnotatedString {
        return parse(
            mentions = mentions,
            hashTags = hashTags,
            emojis = emojis,
            parsePossibleHashtag = parsePossibleHashtag,
            onLinkTargetClick = onLinkTargetClick,
        )
    }

    companion object {
        val empty by lazy { buildRichText("") }
    }
}

internal fun RichText.parse(
    mentions: List<Mention>,
    hashTags: List<HashtagInStatus>,
    emojis: List<Emoji>,
    parsePossibleHashtag: Boolean,
    onLinkTargetClick: OnLinkTargetClick,
): AnnotatedString {
    return HtmlParser.parse(
        document = document,
        emojis = emojis,
        mentions = mentions,
        hashTags = hashTags,
        parsePossibleHashtag = parsePossibleHashtag,
        onLinkTargetClick = onLinkTargetClick,
    )
}
