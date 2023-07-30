package com.zhangke.utopia.status.source

import com.zhangke.utopia.status.uri.StatusProviderUri

interface StatusSource {

    val uri: StatusProviderUri

    val name: String

    val description: String

    val thumbnail: String?
}