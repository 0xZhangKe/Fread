package com.zhangke.framework.architect.http

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpClientEngine(): HttpClientEngine {
    return OkHttp.create {
        preconfigured = GlobalOkHttpClient.client
    }
}