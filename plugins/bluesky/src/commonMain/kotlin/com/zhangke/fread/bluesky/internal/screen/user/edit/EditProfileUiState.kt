package com.zhangke.fread.bluesky.internal.screen.user.edit

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount

data class EditProfileUiState(
    val loggedAccount: BlueskyLoggedAccount?,
    val userName: String,
    val description: String,
    val avatar: String,
    val banner: String,
) {

    companion object {

        fun default(): EditProfileUiState {
            return EditProfileUiState(
                loggedAccount = null,
                userName = "",
                description = "",
                avatar = "",
                banner = "",
            )
        }
    }
}
