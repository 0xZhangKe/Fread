package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubStatusEntity
import com.zhangke.utopia.activitypubapp.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.activitypubapp.user.ActivityPubUserAdapter
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.status.Status
import javax.inject.Inject

class ActivityPubStatusAdapter @Inject constructor(
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val activityPubUserAdapter: ActivityPubUserAdapter,
) {

    fun adapt(entity: ActivityPubStatusEntity, domain: String): Status {
        //fixme temporary code
        return Status.NewBlog(entity.toBlog(domain))
    }

    private fun ActivityPubStatusEntity.toBlog(domain: String): Blog {
        return Blog(
            id = id,
            author = activityPubUserAdapter.adapt(
                account,
            ),
            title = null,
            content = content,
            mediaList = emptyList(),
            date = formatDatetimeToDate(createdAt),
            forwardCount = reblogsCount,
            likeCount = favouritesCount,
            repliesCount = repliesCount,
        )
    }
}