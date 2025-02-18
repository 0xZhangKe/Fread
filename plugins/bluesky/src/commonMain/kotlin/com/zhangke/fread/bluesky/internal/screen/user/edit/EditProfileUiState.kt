package com.zhangke.fread.bluesky.internal.screen.user.edit

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount

data class EditProfileUiState(
    val loggedAccount: BlueskyLoggedAccount?,
    val userName: TextFieldValue,
    val description: TextFieldValue,
    val avatar: String,
    val banner: String,
    val avatarLocalUri: PlatformUri?,
    val bannerLocalUri: PlatformUri?,
    val requesting: Boolean,
) {

    val modified: Boolean
        get() = userName.text != loggedAccount?.userName ||
                description.text != loggedAccount.description ||
                avatarLocalUri != null || bannerLocalUri != null

    companion object {

        fun default(): EditProfileUiState {
            return EditProfileUiState(
                loggedAccount = null,
                userName = TextFieldValue(""),
                description = TextFieldValue(""),
                avatar = "",
                banner = "",
                avatarLocalUri = null,
                bannerLocalUri = null,
                requesting = false,
            )
        }
    }
}
