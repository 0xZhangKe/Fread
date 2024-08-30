package com.zhangke.fread.status.blog

import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.buildRichText
import kotlinx.serialization.Serializable

@Serializable
data class BlogTranslation(
    val content: String,
    val spoilerText: String,
    val poll: Poll?,
    val attachments: List<Attachment>?,
    val detectedSourceLanguage: String,
    val provider: String,
) : java.io.Serializable {

    @Transient
    private var humanizedSpoilerText: RichText? = null

    @Transient
    private var humanizedContent: RichText? = null

    fun getHumanizedSpoilerText(blog: Blog): RichText {
        if (humanizedSpoilerText != null) return humanizedSpoilerText!!
        return buildRichText(
            document = spoilerText,
            mentions = blog.mentions,
            emojis = blog.emojis,
            hashTags = blog.tags,
        ).also { humanizedSpoilerText = it }
    }

    fun getHumanizedContent(blog: Blog): RichText {
        if (humanizedContent != null) return humanizedContent!!
        return buildRichText(
            document = content,
            mentions = blog.mentions,
            emojis = blog.emojis,
            hashTags = blog.tags,
        ).also { humanizedContent = it }
    }

    @Serializable
    data class Poll(
        val id: String,
        val options: List<Option>,
    ) : java.io.Serializable {

        @Serializable
        data class Option(
            val title: String,
        )
    }

    @Serializable
    data class Attachment(
        val id: String,
        val description: String,
    ) : java.io.Serializable
}
