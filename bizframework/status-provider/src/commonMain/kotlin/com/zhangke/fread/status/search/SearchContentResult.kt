package com.zhangke.fread.status.search

import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.source.StatusSource

sealed interface SearchContentResult {

    data class SearchedPlatformSnapshot(val platform: PlatformSnapshot) : SearchContentResult

    data class Platform(val platform: BlogPlatform) : SearchContentResult

    data class Source(val source: StatusSource) : SearchContentResult

    val protocol: StatusProviderProtocol
        get() = when (this) {
            is SearchedPlatformSnapshot -> platform.protocol
            is Source -> source.protocol
            is Platform -> platform.protocol
        }
}

sealed interface SearchedPlatform {

    data class Snapshot(val snapshot: PlatformSnapshot) : SearchedPlatform

    data class Platform(val platform: BlogPlatform) : SearchedPlatform
}
