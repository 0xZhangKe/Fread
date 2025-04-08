package com.zhangke.fread.activitypub.app.internal.screen.explorer

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.StatusUiState

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
