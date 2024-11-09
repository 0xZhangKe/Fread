package com.zhangke.fread.status.search

import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.source.StatusSource

sealed interface SearchContentResult {

    data class ActivityPubPlatform(val platform: BlogPlatform) : SearchContentResult

    data class ActivityPubPlatformSnapshot(val platform: PlatformSnapshot) : SearchContentResult

    data class Source(val source: StatusSource) : SearchContentResult

    val protocol: StatusProviderProtocol
        get() = when (this) {
            is ActivityPubPlatform -> platform.protocol
            is ActivityPubPlatformSnapshot -> platform.protocol
            is Source -> source.protocol
        }
}
