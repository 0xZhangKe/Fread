package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.PlatformSnapshot
import com.zhangke.utopia.status.status.model.Status

sealed interface SearchResult {

    data class Author(val user: BlogAuthor) : SearchResult

    data class Platform(val platform: PlatformSnapshot) : SearchResult

    data class SearchedStatus(val status: Status) : SearchResult

    data class SearchedHashtag(val hashtag: Hashtag) : SearchResult
}
