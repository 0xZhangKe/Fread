package com.zhangke.fread.status.blog

import com.zhangke.framework.serialize.DateSerializer
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.buildRichText
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Blog(
    val id: String,
    val author: BlogAuthor,
    val title: String?,
    val description: String?,
    val content: String,
    val url: String,
    @Serializable(with = DateSerializer::class) val date: Date,
    val forwardCount: Int?,
    val likeCount: Int?,
    val repliesCount: Int?,
    val sensitive: Boolean,
    val spoilerText: String,
    val platform: BlogPlatform,
    val mediaList: List<BlogMedia>,
    val emojis: List<Emoji>,
    val mentions: List<Mention>,
    val tags: List<HashtagInStatus>,
    val pinned: Boolean = false,
    val poll: BlogPoll?,
    val visibility: StatusVisibility,
    val card: PreviewCard? = null,
    val isSelf: Boolean = false,
    @Serializable(with = DateSerializer::class) val editedAt: Date? = null,
    val application: PostingApplication? = null,
) : java.io.Serializable {

    val humanizedSpoilerText: RichText by lazy {
        buildRichText(
            document = spoilerText,
            mentions = mentions,
            emojis = emojis,
            hashTags = tags,
        )
    }

    val humanizedContent: RichText by lazy {
        buildRichText(
            document = content,
            mentions = mentions,
            emojis = emojis,
            hashTags = tags,
        )
    }

    val humanizedDescription: RichText by lazy {
        buildRichText(
            document = description.orEmpty(),
            mentions = mentions,
            emojis = emojis,
            hashTags = tags,
        )
    }
}
