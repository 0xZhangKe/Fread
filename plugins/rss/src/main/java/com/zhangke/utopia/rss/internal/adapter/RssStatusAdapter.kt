package com.zhangke.utopia.rss.internal.adapter

import com.zhangke.utopia.rss.internal.platform.RssPlatformTransformer
import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.rss.RssItem
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class RssStatusAdapter @Inject constructor(
    private val blogAuthorAdapter: BlogAuthorAdapter,
    private val rssPlatformTransformer: RssPlatformTransformer,
) {

    fun toStatus(
        uriInsight: RssUriInsight,
        channel: RssChannel,
        rssItem: RssItem,
    ): Status {
        return Status.NewBlog(
            blog = rssItem.toBlog(uriInsight, channel),
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

    private fun RssItem.toBlog(uriInsight: RssUriInsight, channel: RssChannel): Blog {
        return Blog(
            id = this.id,
            author = blogAuthorAdapter.createAuthor(uriInsight, channel),
            title = this.title,
            content = this.description.orEmpty(),
            date = this.pubDate,
            forwardCount = null,
            likeCount = null,
            repliesCount = null,
            sensitive = false,
            spoilerText = "",
            platform = rssPlatformTransformer.create(uriInsight, channel),
            mediaList = emptyList(),
            poll = null,
        )
    }
}
