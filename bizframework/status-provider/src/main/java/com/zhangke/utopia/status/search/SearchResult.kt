package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusSource

sealed interface SearchResult {

    data class Source(val source: StatusSource): SearchResult
}
