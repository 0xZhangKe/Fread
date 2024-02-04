package com.zhangke.utopia.common.status.model

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform

sealed interface SearchResultUiState {

    data class Author(val author: BlogAuthor) : SearchResultUiState

    data class Platform(val platform: BlogPlatform) : SearchResultUiState

    data class SearchedStatus(val status: StatusUiState) : SearchResultUiState

    data class SearchedHashtag(val hashtag: Hashtag) : SearchResultUiState
}
