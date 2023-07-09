package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.activitypub.entry.ActivityPubStatusEntity
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogAuthor
import com.zhangke.utopia.status.status.Status
import org.joda.time.format.ISODateTimeFormat
import java.util.*
import javax.inject.Inject

class ActivityPubStatusAdapter @Inject constructor() {

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
            date = formatActivityPubDate(createdAt),
            forwardCount = reblogsCount,
            likeCount = favouritesCount,
            repliesCount = repliesCount,
        )
    }

    private fun formatActivityPubDate(dateTimeText: String): Date {
        return ISODateTimeFormat.dateTime().parseDateTime(dateTimeText).toDate()
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