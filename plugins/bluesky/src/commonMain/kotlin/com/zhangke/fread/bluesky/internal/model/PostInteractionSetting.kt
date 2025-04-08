package com.zhangke.fread.bluesky.internal.model

import app.bsky.graph.ListView

data class PostInteractionSetting(
    val allowQuote: Boolean,
    val replySetting: ReplySetting,
) {

    companion object {

        fun default(): PostInteractionSetting {
            return PostInteractionSetting(
                allowQuote = true,
                replySetting = ReplySetting.Everybody,
            )
        }
    }
}

sealed interface ReplySetting {

    data object Nobody : ReplySetting

    data object Everybody : ReplySetting

    data class Combined(val options: List<CombineOption>) : ReplySetting

    val combinedMentions: Boolean
        get() = this is Combined && options.contains(CombineOption.Mentioned)

    val combinedFollowing: Boolean
        get() = this is Combined && options.contains(CombineOption.Following)

    val combinedFollowers: Boolean
        get() = this is Combined && options.contains(CombineOption.Followers)

    sealed interface CombineOption {

        data object Mentioned : CombineOption

        data object Following : CombineOption

        data object Followers : CombineOption

        data class UserInList(val listView: ListView) : CombineOption
    }
}
