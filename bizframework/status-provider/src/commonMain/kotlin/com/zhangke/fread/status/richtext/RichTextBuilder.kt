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
): RichText {
    return RichText(
        document = document,
        mentions = mentions,
        hashTags = hashTags,
        emojis = emojis,
        facets = facets,
    )
}

suspend fun Blog.preParseBlog() {
    author.humanizedName.parse()
    humanizedContent.parse()
    humanizedSpoilerText.parse()
    humanizedDescription.parse()
    formattingDisplayTime.parse()
}

suspend fun Status.preParseStatus() {
    triggerAuthor.humanizedName.parse()
    intrinsicBlog.preParseBlog()
}

suspend fun StatusUiState.preParseStatusUiState() {
    status.triggerAuthor.humanizedName.parse()
    status.intrinsicBlog.preParseBlog()
}

suspend fun List<Status>.preParseStatusList() {
    forEach {
        it.preParseStatus()
    }
}

suspend fun List<StatusUiState>.preParse() {
    forEach {
        it.preParseStatusUiState()
    }
}
