package com.zhangke.utopia.common.status.model

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.platform.BlogPlatform

sealed interface SearchResultUiState {

    data class Author(val role: IdentityRole, val author: BlogAuthor) : SearchResultUiState

    data class Platform(val role: IdentityRole, val platform: BlogPlatform) : SearchResultUiState

    data class SearchedStatus(val status: StatusUiState) : SearchResultUiState

    data class SearchedHashtag(val role: IdentityRole, val hashtag: Hashtag) : SearchResultUiState
}
