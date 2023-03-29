package com.zhangke.utopia.status.source

interface StatusSource {

    val uri: String

    val nickName: String

    val description: String

    val thumbnail: String?

    suspend fun saveToLocal()
}