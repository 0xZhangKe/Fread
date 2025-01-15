package com.zhangke.fread.activitypub.app.internal.richtext

import androidx.compose.ui.text.AnnotatedString
import com.zhangke.framework.utils.PlatformTransient
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.IRichText
import com.zhangke.fread.status.richtext.OnLinkTargetClick

data class ActivityPubRichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    val mentions: List<Mention>,
    val hashTags: List<HashtagInStatus>,
    val emojis: List<Emoji>,
    val parsePossibleHashtag: Boolean = false,
): IRichText {

    @PlatformTransient
    private var clickableDelegate: OnLinkTargetClick = { target ->
        onLinkTargetClick?.invoke(target)
    }

    override val originText: String = document

    override var onLinkTargetClick: OnLinkTargetClick
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun parse(): AnnotatedString {
        TODO("Not yet implemented")
    }
}
