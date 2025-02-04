package com.zhangke.fread.common.status.model

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform

sealed interface SearchResultUiState {

    data class Author(val role: IdentityRole, val author: BlogAuthor) : SearchResultUiState

    data class Platform(val role: IdentityRole, val platform: BlogPlatform) : SearchResultUiState

    data class SearchedStatus(val status: StatusUiState) : SearchResultUiState

    data class SearchedHashtag(val role: IdentityRole, val hashtag: Hashtag) : SearchResultUiState
}
