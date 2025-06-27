package com.zhangke.fread.common.status.model

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform

sealed interface SearchResultUiState {

    data class Author(val locator: PlatformLocator, val author: BlogAuthor) : SearchResultUiState

    data class Platform(val locator: PlatformLocator, val platform: BlogPlatform) :
        SearchResultUiState

    data class SearchedStatus(val status: StatusUiState) : SearchResultUiState

    data class SearchedHashtag(val locator: PlatformLocator, val hashtag: Hashtag) :
        SearchResultUiState
}
