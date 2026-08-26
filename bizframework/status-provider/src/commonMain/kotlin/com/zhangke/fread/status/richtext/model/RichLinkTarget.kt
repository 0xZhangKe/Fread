package com.zhangke.fread.status.richtext.model

import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import kotlinx.serialization.Serializable

@Serializable
sealed interface RichLinkTarget {

    @Serializable
    data class UrlTarget(val url: String) : RichLinkTarget

    @Serializable
    data class MentionTarget(val mention: Mention) : RichLinkTarget

    @Serializable
    data class MentionDidTarget(val did: String) : RichLinkTarget

    @Serializable
    data class HashtagTarget(val hashtag: HashtagInStatus) : RichLinkTarget

    @Serializable
    data class MaybeHashtagTarget(val hashtag: String) : RichLinkTarget
}