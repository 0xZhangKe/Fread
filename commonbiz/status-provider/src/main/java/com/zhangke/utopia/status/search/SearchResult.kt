package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.model.UtopiaAuthor

sealed interface SearchResult {

    data class Author(val author: UtopiaAuthor): SearchResult
}
