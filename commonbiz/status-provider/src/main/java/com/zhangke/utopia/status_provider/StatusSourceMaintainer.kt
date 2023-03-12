package com.zhangke.utopia.status_provider

interface StatusSourceMaintainer {

    val url: String

    val name: String

    val description: String

    val thumbnail: String?

    val sourceList: List<StatusSource>
}