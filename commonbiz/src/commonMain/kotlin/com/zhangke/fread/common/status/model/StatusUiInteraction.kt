package com.zhangke.fread.common.status.model

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.status.status.model.StatusInteraction
import kotlinx.serialization.Serializable

@Serializable
sealed class StatusUiInteraction : PlatformSerializable {

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
            is Pin -> interaction
            is Edit -> interaction
        }

    @Serializable
    data class Like(val interaction: StatusInteraction.Like) : StatusUiInteraction(),
        PlatformSerializable {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = interaction.likeCount.countToLabel()

        override val highLight: Boolean get() = interaction.liked
    }

    @Serializable
    data class Comment(val interaction: StatusInteraction.Comment) : StatusUiInteraction(),
        PlatformSerializable {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = interaction.commentCount.countToLabel()
    }

    @Serializable
    data class Forward(val interaction: StatusInteraction.Forward) : StatusUiInteraction(),
        PlatformSerializable {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = interaction.forwardCount.countToLabel()

        override val highLight: Boolean
            get() = interaction.forwarded
    }

    @Serializable
    data class Bookmark(val interaction: StatusInteraction.Bookmark) : StatusUiInteraction(),
        PlatformSerializable {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = null

        override val highLight: Boolean get() = interaction.bookmarked
    }

    @Serializable
    data class Delete(val interaction: StatusInteraction.Delete) : StatusUiInteraction(),
        PlatformSerializable {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = null
    }

    @Serializable
    data class Pin(val interaction: StatusInteraction.Pin) : StatusUiInteraction(),
        PlatformSerializable {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = null
    }

    @Serializable
    data class Edit(val interaction: StatusInteraction.Edit) : StatusUiInteraction(),
        PlatformSerializable {

        override val enabled: Boolean get() = interaction.enable

        override val label: String? get() = null
    }

    @Serializable
    data object Share : StatusUiInteraction(), PlatformSerializable {

        private fun readResolve(): Any = Share

        override val enabled: Boolean get() = true

        override val label: String? get() = null
    }
}

private fun Int.countToLabel(): String? {
    return when {
        this <= 0 -> null
        else -> this.formatToHumanReadable()
    }
}
