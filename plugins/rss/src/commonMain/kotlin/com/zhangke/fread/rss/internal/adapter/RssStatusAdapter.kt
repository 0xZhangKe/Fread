package com.zhangke.fread.rss.internal.adapter

import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.rss.internal.model.RssChannelItem
import com.zhangke.fread.rss.internal.model.RssSource
import com.zhangke.fread.rss.internal.platform.RssPlatformTransformer
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject

class RssStatusAdapter @Inject constructor(
    private val blogAuthorAdapter: BlogAuthorAdapter,
    private val rssPlatformTransformer: RssPlatformTransformer,
) {

    private val meaninglessTitleList = listOf(
        "unknown",
        "null",
    )

    suspend fun toStatus(
        uriInsight: RssUriInsight,
        source: RssSource,
        rssItem: RssChannelItem,
    ): Status {
        return Status.NewBlog(
            blog = rssItem.toBlog(uriInsight, source),
            supportInteraction = listOf(
                // TODO support book mark
//                StatusInteraction.Bookmark(
//                    bookmarkCount = null,
//                    bookmarked = false,
//                    enable = true,
//                ),
            ),
        )
    }

    private suspend fun RssChannelItem.toBlog(uriInsight: RssUriInsight, source: RssSource): Blog {
        return Blog(
            id = this.id,
            author = blogAuthorAdapter.createAuthor(uriInsight, source),
            title = removeMeaninglessTitle(this.title),
            url = this.link.ifNullOrEmpty { uriInsight.url },
            content = this.content.ifNullOrEmpty { this.description.ifNullOrEmpty { this.link.orEmpty() } },
            description = this.description,
            date = this.pubDate,
            forwardCount = null,
            likeCount = null,
            repliesCount = null,
            sensitive = false,
            spoilerText = "",
            platform = rssPlatformTransformer.create(uriInsight, source),
            mediaList = emptyList(),
            poll = null,
            emojis = emptyList(),
            mentions = emptyList(),
            tags = emptyList(),
            pinned = false,
            visibility = StatusVisibility.PUBLIC,
            card = null,
            language = null,
        )
    }

    private fun removeMeaninglessTitle(title: String): String? {
        return if (meaninglessTitleList.contains(title.lowercase())) {
            null
        } else {
            title
        }
    }
}
