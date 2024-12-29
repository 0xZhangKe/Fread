package com.zhangke.fread.bluesky.internal.screen.add

data class AddBlueskyContentUiState(
    val hosting: String,
    val username: String,
    val password: String,
    val logging: Boolean,
) {

    val canLogin: Boolean get() = hosting.isNotBlank() && username.isNotBlank() && password.isNotBlank() && !logging

    companion object {

        fun default(
            hosting: String = "",
            username: String = "",
            password: String = "",
            logging: Boolean = false,
        ): AddBlueskyContentUiState {
            return AddBlueskyContentUiState(
                hosting = hosting,
                username = username,
                password = password,
                logging = logging,
            )
        }
    }
}
