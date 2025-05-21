package com.zhangke.fread.rss

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.publish.IPublishBlogManager
import com.zhangke.fread.status.publish.PublishingPost
import me.tatarka.inject.annotations.Inject

class RssPublishManager @Inject constructor() : IPublishBlogManager {

    override suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules>? {
        return null
    }

    override suspend fun publish(account: LoggedAccount, post: PublishingPost): Result<Unit>? {
        return null
    }
}
