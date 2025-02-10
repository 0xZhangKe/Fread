package com.zhangke.fread.status.search

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status

sealed interface SearchResult {

    data class Author(val user: BlogAuthor) : SearchResult

    data class Platform(val platform: BlogPlatform) : SearchResult

    data class SearchedStatus(val status: StatusUiState) : SearchResult

    data class SearchedHashtag(val hashtag: Hashtag) : SearchResult
}
