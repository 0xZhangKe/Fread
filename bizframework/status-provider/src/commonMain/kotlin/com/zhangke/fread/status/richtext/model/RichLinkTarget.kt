package com.zhangke.fread.status.richtext.model

import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention

sealed interface RichLinkTarget {

    data class UrlTarget(val url: String) : RichLinkTarget

    data class MentionTarget(val mention: Mention) : RichLinkTarget

    data class HashtagTarget(val hashtag: HashtagInStatus) : RichLinkTarget

    data class MaybeHashtagTarget(val hashtag: String) : RichLinkTarget
}