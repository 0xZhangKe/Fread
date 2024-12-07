package com.zhangke.fread.bluesky.internal.screen.add

data class AddBlueskyContentUiState(
    val hosting: String,
    val username: String,
    val password: String,
) {

    companion object {

        fun default(
            hosting: String = "",
            username: String = "",
            password: String = "",
        ): AddBlueskyContentUiState {
            return AddBlueskyContentUiState(
                hosting = hosting,
                username = username,
                password = password,
            )
        }
    }
}
