package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.status.model.Status

fun buildRichText(
    document: String,
    mentions: List<Mention> = emptyList(),
    hashTags: List<HashtagInStatus> = emptyList(),
    emojis: List<Emoji> = emptyList(),
    facets: List<Facet> = emptyList(),
    parsePossibleHashtag: Boolean = false,
): RichText {
    return RichText(
        document = document,
        mentions = mentions,
        hashTags = hashTags,
        emojis = emojis,
        facets = facets,
        parsePossibleHashtag = parsePossibleHashtag,
    )
}

suspend fun Blog.preParseRichText() {
    author.humanizedName.parse()
    humanizedContent.parse()
    humanizedSpoilerText.parse()
    humanizedDescription.parse()
    formattingDisplayTime.parse()
}

suspend fun Status.preParseRichText() {
    triggerAuthor.humanizedName.parse()
    intrinsicBlog.preParseRichText()
}

suspend fun StatusUiState.preParseRichText() {
    status.triggerAuthor.humanizedName.parse()
    status.intrinsicBlog.preParseRichText()
}

suspend fun List<Status>.preParseRichText() {
    forEach {
        it.preParseRichText()
    }
}

suspend fun List<StatusUiState>.preParseRichText() {
    forEach {
        it.preParseRichText()
    }
}
