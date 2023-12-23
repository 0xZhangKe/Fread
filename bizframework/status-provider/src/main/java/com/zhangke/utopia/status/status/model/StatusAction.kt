package com.zhangke.utopia.status.status.model

import kotlinx.serialization.Serializable

@Serializable
sealed class StatusAction {

    abstract val enable: Boolean

    @Serializable
    data class Like(
        val likeCount: Int,
        val liked: Boolean,
        override val enable: Boolean,
    ) : StatusAction()

    @Serializable
    data class Forward(
        val forwardCount: Int,
        override val enable: Boolean,
    ) : StatusAction()

    @Serializable
    data class Comment(
        val commentCount: Int,
        override val enable: Boolean,
    ) : StatusAction()

    @Serializable
    data class Bookmark(
        val bookmarkCount: Int?,
        val bookmarked: Boolean,
        override val enable: Boolean,
    ) : StatusAction()

    @Serializable
    class Delete(
        override val enable: Boolean,
    ) : StatusAction()
}
