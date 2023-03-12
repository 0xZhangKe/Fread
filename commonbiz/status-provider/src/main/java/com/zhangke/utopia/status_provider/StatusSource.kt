package com.zhangke.utopia.status_provider

interface StatusSource {

    val uri: String

    val nickName: String

    val description: String

    val thumbnail: String?

    suspend fun saveToLocal()

    suspend fun requestMaintainer(): StatusSourceMaintainer
}