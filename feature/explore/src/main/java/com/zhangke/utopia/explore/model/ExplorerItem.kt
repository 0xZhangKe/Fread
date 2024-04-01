package com.zhangke.utopia.explore.model

import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag

sealed interface ExplorerItem {

    val id: String

    data class ExplorerStatus(val status: StatusUiState) : ExplorerItem {
        override val id: String
            get() = status.status.id
    }

    data class ExplorerHashtag(val hashtag: Hashtag) : ExplorerItem {
        override val id: String
            get() = hashtag.name
    }

    data class ExplorerUser(val user: BlogAuthor, val following: Boolean) : ExplorerItem {
        override val id: String
            get() = user.uri.toString()
    }
}
