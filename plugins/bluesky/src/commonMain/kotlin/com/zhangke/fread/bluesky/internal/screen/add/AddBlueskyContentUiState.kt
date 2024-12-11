package com.zhangke.fread.bluesky.internal.screen.add

import com.zhangke.framework.utils.appDebuggable
import com.zhangke.framework.utils.ifDebugging

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
                username = if (appDebuggable){
                    "kezhang404@gmail.com"
                }else{
                    ""
                },
                password = if (appDebuggable){
                    "Tv=Ve,d9\$<YsQ@ym0=1m"
                }else{
                    ""
                },
                logging = logging,
            )
        }
    }
}
