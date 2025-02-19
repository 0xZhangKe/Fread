package com.zhangke.fread.bluesky.internal.model

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

    data class Combined(val options: List<CombineOptions>) : ReplySetting

    sealed interface CombineOptions {

        data object Mentioned : CombineOptions

        data object Following : CombineOptions

        data object Followers : CombineOptions

        data class UserInList(val list: String) : CombineOptions
    }
}
