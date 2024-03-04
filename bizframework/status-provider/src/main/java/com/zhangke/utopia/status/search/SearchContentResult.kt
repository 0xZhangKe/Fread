package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.source.StatusSource

sealed interface SearchContentResult {

    data class ActivityPubPlatform(val platform: BlogPlatform) : SearchContentResult

    data class Source(val source: StatusSource) : SearchContentResult
}
