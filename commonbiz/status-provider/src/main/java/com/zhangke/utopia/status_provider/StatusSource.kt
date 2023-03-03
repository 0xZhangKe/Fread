package com.zhangke.utopia.status_provider

interface StatusSource {

    val uri: String

    val provider: StatusProvider

    suspend fun requestAdd(addFunction: suspend (valid: Boolean) -> Unit)
}