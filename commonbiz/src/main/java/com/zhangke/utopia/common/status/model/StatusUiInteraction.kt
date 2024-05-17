package com.zhangke.utopia.common.status.model

import com.zhangke.utopia.status.status.model.StatusInteraction

sealed class StatusUiInteraction {

    abstract val enabled: Boolean

    abstract val label: String?

    open val highLight: Boolean = false

    val statusInteraction: StatusInteraction?
        get() = when (this) {
            is Like -> interaction
            is Comment -> interaction
            is Forward -> interaction
            is Bookmark -> interaction
            is Delete -> interaction
            is Share -> null
        }

    data class Like(val interaction: StatusInteraction.Like) : StatusUiInteraction() {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = interaction.likeCount.countToLabel()

        override val highLight: Boolean get() = interaction.liked
    }

    data class Comment(val interaction: StatusInteraction.Comment) : StatusUiInteraction() {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = interaction.commentCount.countToLabel()
    }

    data class Forward(val interaction: StatusInteraction.Forward) : StatusUiInteraction() {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = interaction.forwardCount.countToLabel()

        override val highLight: Boolean
            get() = interaction.forwarded
    }

    data class Bookmark(val interaction: StatusInteraction.Bookmark) : StatusUiInteraction() {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = null

        override val highLight: Boolean get() = interaction.bookmarked
    }

    data class Delete(val interaction: StatusInteraction.Delete) : StatusUiInteraction() {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = null
    }

    data object Share : StatusUiInteraction() {

        override val enabled: Boolean get() = true

        override val label: String? get() = null
    }
}

private fun Int.countToLabel(): String? {
    return when {
        this <= 0 -> null
        else -> this.toString()
    }
}
