package com.zhangke.utopia.status_provider

import com.google.gson.JsonObject

interface BlogSourceRestorer {

    fun restoreBlogSource(
        metaSourceInfo: MetaSourceInfo,
        sourceServer: String,
        protocol: String,
        sourceName: String,
        sourceDescription: String?,
        avatar: String?,
        extra: JsonObject?
    ): StatusSource?
}