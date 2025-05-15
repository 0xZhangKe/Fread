package com.zhangke.fread.rss

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.publish.IPublishBlogManager

class RssPublishManager : IPublishBlogManager {

    override suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules>? {
        return null
    }
}
