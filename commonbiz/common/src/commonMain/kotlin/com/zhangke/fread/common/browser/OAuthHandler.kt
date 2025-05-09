package com.zhangke.fread.common.browser

expect class OAuthHandler {
    suspend fun startOAuth(url: String): String
}
