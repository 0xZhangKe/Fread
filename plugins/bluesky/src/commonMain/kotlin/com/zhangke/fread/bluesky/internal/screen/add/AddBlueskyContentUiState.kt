package com.zhangke.fread.bluesky.internal.screen.add

data class AddBlueskyContentUiState(
    val loginMode: Boolean,
    val hosting: String,
    val username: String,
    val password: String,
    val factorToken: String,
    val logging: Boolean,
    val avatar: String?,
    val displayName: String?,
    val handle: String?,
    val authFactorRequired: Boolean,
) {

    val loginToSpecAccount: Boolean
        get() = loginMode && !handle.isNullOrEmpty()

    companion object {

        fun default(
            loginMode: Boolean,
            hosting: String = "",
            username: String = "",
            password: String = "",
            avatar: String? = null,
            displayName: String? = null,
            handle: String? = null,
            logging: Boolean = false,
        ): AddBlueskyContentUiState {
            return AddBlueskyContentUiState(
                loginMode = loginMode,
                hosting = hosting,
                username = username,
                password = password,
                logging = logging,
                factorToken = "",
                authFactorRequired = false,
                avatar = avatar,
                displayName = displayName,
                handle = handle,
            )
        }
    }
}
