package com.zhangke.framework.architect.http

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClientEngine(): HttpClientEngine {
    return Darwin.create()
}