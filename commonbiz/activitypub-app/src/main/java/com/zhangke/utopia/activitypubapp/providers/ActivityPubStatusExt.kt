package com.zhangke.utopia.activitypubapp.providers

import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.activitypub.entry.ActivityPubStatus
import com.zhangke.utopia.status_provider.Blog
import com.zhangke.utopia.status_provider.BlogAuthor
import com.zhangke.utopia.status_provider.Status
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.util.*
import kotlin.concurrent.getOrSet


internal fun Result<List<ActivityPubStatus>>.toStatus(domain: String): Result<List<Status>> {
    return map { list ->
        list.map { it.toStatus(domain) }
    }
}

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

private val activityPubDateTimeFormatterLocal = ThreadLocal<DateTimeFormatter>()

private val activityPubDateTimeFormatter: DateTimeFormatter
    get() = activityPubDateTimeFormatterLocal.getOrSet {
        ISODateTimeFormat.dateTime()
    }

private fun formatActivityPubDate(dateTimeText: String): Date {
    return activityPubDateTimeFormatter.parseDateTime(dateTimeText).toDate()
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