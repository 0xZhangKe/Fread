package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.activitypub.entry.ActivityPubStatusEntity
import com.zhangke.utopia.activitypubapp.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogAuthor
import com.zhangke.utopia.status.status.Status
import javax.inject.Inject

class ActivityPubStatusAdapter @Inject constructor(
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase
) {

    fun adapt(statusAdapter: ActivityPubStatusEntity, domain: String): Status {
        //fixme temporary code
        return Status.NewBlog(statusAdapter.toBlog(domain))
    }

    private fun ActivityPubStatusEntity.toBlog(domain: String): Blog {
        return Blog(
            id = id,
            author = account.toAuthor(domain),
            supportedAction = emptyList(),
            title = null,
            content = content,
            mediaList = emptyList(),
            date = formatDatetimeToDate(createdAt),
            forwardCount = reblogsCount,
            likeCount = favouritesCount,
            repliesCount = repliesCount,
        )
    }

    private fun ActivityPubAccountEntity.toAuthor(domain: String): BlogAuthor {
        return BlogAuthor(
            name = username,
            avatar = avatar,
            id = "${acct}@$domain",
            description = note,
            homePage = url,
        )
    }
}