package com.zhangke.fread.status.status.model

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
sealed class StatusInteraction : PlatformSerializable {

    abstract val enable: Boolean

    @Serializable
    data class Like(
        val likeCount: Int,
        val liked: Boolean,
        override val enable: Boolean,
    ) : StatusInteraction(), PlatformSerializable

    @Serializable
    data class Forward(
        val forwardCount: Int,
        val forwarded: Boolean,
        override val enable: Boolean,
    ) : StatusInteraction(), PlatformSerializable

    @Serializable
    data class Comment(
        val commentCount: Int,
        override val enable: Boolean,
    ) : StatusInteraction(), PlatformSerializable

    @Serializable
    data class Bookmark(
        val bookmarkCount: Int?,
        val bookmarked: Boolean,
        override val enable: Boolean,
    ) : StatusInteraction(), PlatformSerializable

    @Serializable
    class Delete(
        override val enable: Boolean,
    ) : StatusInteraction(), PlatformSerializable

    @Serializable
    class Pin(
        val pinned: Boolean,
        override val enable: Boolean,
    ) : StatusInteraction(), PlatformSerializable
}
