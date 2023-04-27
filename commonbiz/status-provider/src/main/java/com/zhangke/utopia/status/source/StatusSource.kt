package com.zhangke.utopia.status.source

interface StatusSource {

    val uri: String

    val name: String

    val description: String

    val thumbnail: String?
}