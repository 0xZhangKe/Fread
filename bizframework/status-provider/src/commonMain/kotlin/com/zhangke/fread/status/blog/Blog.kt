package com.zhangke.fread.status.blog

import com.zhangke.framework.datetime.Instant
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.BlogFiltered
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.FormattingTime
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
    // http link
    val link: String,
    val createAt: Instant,
    val formattedCreateAt: String,
    val supportEdit: Boolean,
    val like: Like,
    val forward: Forward,
    val bookmark: Bookmark,
    val reply: Reply,
    val quote: Quote,
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
    val facets: List<Facet>,
    val pinned: Boolean = false,
    val poll: BlogPoll?,
    val visibility: StatusVisibility,
    val embeds: List<BlogEmbed> = emptyList(),
    val supportTranslate: Boolean = false,
    val editedAt: Instant? = null,
    val formattedEditAt: String? = null,
    val application: PostingApplication? = null,
    val filtered: List<BlogFiltered>? = null,
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
            facets = facets,
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

    val formattingDisplayTime: FormattingTime by lazy {
        FormattingTime(createAt)
    }

    val sensitiveByFilter: Boolean
        get() {
            if (filtered.isNullOrEmpty()) return false
            return filtered.any {
                it.action == BlogFiltered.FilterAction.WARN || it.action == BlogFiltered.FilterAction.BLUR
            }
        }

    @Serializable
    data class Like(
        val support: Boolean,
        val likedCount: Long? = null,
        val liked: Boolean? = null,
    )

    @Serializable
    data class Forward(
        val support: Boolean,
        val forwardCount: Long? = null,
        val forward: Boolean? = null,
    )

    @Serializable
    data class Bookmark(
        val support: Boolean,
        val bookmarked: Boolean? = null,
    )

    @Serializable
    data class Reply(
        val support: Boolean,
        val repliesCount: Long? = null,
    )

    @Serializable
    data class Quote(
        val support: Boolean,
    )
}
