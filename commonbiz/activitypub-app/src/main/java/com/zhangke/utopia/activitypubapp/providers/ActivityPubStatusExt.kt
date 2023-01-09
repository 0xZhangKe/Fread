package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.activitypub.entry.ActivityPubStatus
import com.zhangke.utopia.blogprovider.Blog
import com.zhangke.utopia.blogprovider.BlogAuthor
import com.zhangke.utopia.blogprovider.Status
import org.joda.time.format.ISODateTimeFormat
import java.util.*

internal fun ActivityPubStatus.toStatus(domain: String): Status {
    //fixme temporary code
    return Status.NewBlog(toBlog(domain))
}

private fun ActivityPubStatus.toBlog(domain: String): Blog {
    return Blog(
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

private val activityPubDateTimeFormatterLocal = ThreadLocal.withInitial {
    ISODateTimeFormat.dateTime()
}

private fun formatActivityPubDate(dateTimeText: String): Date {
    return activityPubDateTimeFormatterLocal.get()!!.parseDateTime(dateTimeText).toDate()
}

private fun ActivityPubAccount.toAuthor(domain: String): BlogAuthor {
    return BlogAuthor(
        name = username,
        avatar = avatar,
        id = "${acct}@$domain",
        description = note,
        homePage = url,
    )
}