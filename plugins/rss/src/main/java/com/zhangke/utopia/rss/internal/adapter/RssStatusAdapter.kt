package com.zhangke.utopia.rss.internal.adapter

import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.utopia.rss.internal.rss.RssItem
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class RssStatusAdapter @Inject constructor() {

    fun toStatus(rssItem: RssItem): Status {
        return Status.NewBlog(
            blog = rssItem.toBlog(),
            supportInteraction = listOf(
                // TODO support book mark
                StatusInteraction.Bookmark(
                    bookmarkCount = null,
                    bookmarked = false,
                    enable = true,
                ),
            ),
        )
    }

    private fun RssItem.toBlog(): Blog {
        return Blog(
            id = getRssItemId(this),

            )
    }

    private fun getRssItemId(rssItem: RssItem): String {
        if (rssItem.guid.isNullOrEmpty().not()) {
            return rssItem.guid!!
        }
        return rssItem.link.ifNullOrEmpty {
            rssItem.title.ifNullOrEmpty { System.currentTimeMillis().toString() }
        }
    }
}
