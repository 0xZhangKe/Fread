package com.zhangke.fread.status.richtext

import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.status.model.Status

fun buildRichText(
    document: String,
    mentions: List<Mention> = emptyList(),
    hashTags: List<HashtagInStatus> = emptyList(),
    emojis: List<Emoji> = emptyList(),
    parsePossibleHashtag: Boolean = false,
): RichText {
    return RichText(
        document = document,
        mentions = mentions,
        hashTags = hashTags,
        emojis = emojis,
        parsePossibleHashtag = parsePossibleHashtag,
    )
}

fun Blog.preParseRichText() {
    author.humanizedName.parseRichText
    humanizedContent.parseRichText
    humanizedSpoilerText.parseRichText
    humanizedDescription.parseRichText
}

fun Status.preParseRichText() {
    triggerAuthor.humanizedName.parseRichText
    intrinsicBlog.preParseRichText()
}

fun List<Status>.preParseRichText() {
    forEach {
        it.preParseRichText()
    }
}
