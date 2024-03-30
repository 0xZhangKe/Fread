package com.zhangke.utopia.explore.model

import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag

sealed interface ExplorerItem {

    data class ExplorerStatus(val status: StatusUiState) : ExplorerItem

    data class ExplorerHashtag(val hashtag: Hashtag) : ExplorerItem

    data class ExplorerUser(val user: BlogAuthor) : ExplorerItem
}
