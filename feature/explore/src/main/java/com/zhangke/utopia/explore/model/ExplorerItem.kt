package com.zhangke.utopia.explore.model

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.status.model.Status

sealed interface ExplorerItem {

    data class ExplorerStatus(val status: Status) : ExplorerItem

    data class ExplorerHashtag(val hashtag: Hashtag) : ExplorerItem

    data class ExplorerUser(val userId: BlogAuthor) : ExplorerItem
}
