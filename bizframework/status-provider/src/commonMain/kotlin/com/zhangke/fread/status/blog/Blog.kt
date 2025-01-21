package com.zhangke.fread.status.blog

import com.zhangke.framework.datetime.Instant
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.buildRichText
import kotlinx.serialization.Serializable

@Serializable
data class Blog(
    val id: String,
    val author: BlogAuthor,
    val title: String?,
    val description: String?,
    val content: String,
    val url: String,
    val date: Instant,
    val forwardCount: Long?,
    val likeCount: Long?,
    val repliesCount: Long?,
    val sensitive: Boolean,
    val spoilerText: String,
    /**
     * ISO 639 Part 1 two-letter language code
     */
    val language: String? = null,
    // 对于 Bluesky 来说，个人数据应该通过 DID 获取 PDS endpoint，而不是直接使用 baseUrl
    val platform: BlogPlatform,
    val mediaList: List<BlogMedia>,
    val emojis: List<Emoji>,
    val mentions: List<Mention>,
    val tags: List<HashtagInStatus>,
    val pinned: Boolean = false,
    val poll: BlogPoll?,
    val visibility: StatusVisibility,
    val embed: BlogEmbed? = null,
    val isSelf: Boolean = false,
    val supportTranslate: Boolean = false,
    val editedAt: Instant? = null,
    val application: PostingApplication? = null,
) : PlatformSerializable {

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
