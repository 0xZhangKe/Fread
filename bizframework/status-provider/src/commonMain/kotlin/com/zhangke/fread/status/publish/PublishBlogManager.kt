package com.zhangke.fread.status.publish

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules

class PublishBlogManager(
    private val managerList: List<IPublishBlogManager>,
) {

    suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules> {
        return managerList.firstNotNullOf { it.getPublishBlogRules(account) }
    }
}

interface IPublishBlogManager {

    suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules>?

}
