package com.zhangke.fread.status.richtext

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val mentions: List<Mention>,
    private val hashTags: List<HashtagInStatus>,
    private val emojis: List<Emoji>,
    private val parsePossibleHashtag: Boolean = false,
) : PlatformSerializable {

    @Transient
    private var charSequence: CharSequence? = null

    fun parse(): CharSequence {
        charSequence?.let { return it }
        return parse(
            mentions = mentions,
            hashTags = hashTags,
            emojis = emojis,
            parsePossibleHashtag = parsePossibleHashtag,
        ).also {
            charSequence = it
        }
    }

    companion object {
        val empty by lazy { buildRichText("") }
    }
}

internal expect fun RichText.parse(
    mentions: List<Mention>,
    hashTags: List<HashtagInStatus>,
    emojis: List<Emoji>,
    parsePossibleHashtag: Boolean,
): CharSequence
