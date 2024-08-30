package com.zhangke.fread.status.search

import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.source.StatusSource

sealed interface SearchContentResult {

    data class ActivityPubPlatform(val platform: BlogPlatform) : SearchContentResult

    data class ActivityPubPlatformSnapshot(val platform: PlatformSnapshot) : SearchContentResult

    data class Source(val source: StatusSource) : SearchContentResult
}
