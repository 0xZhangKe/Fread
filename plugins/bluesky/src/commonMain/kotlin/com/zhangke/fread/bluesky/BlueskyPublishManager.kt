package com.zhangke.fread.bluesky

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.publish.IPublishBlogManager

class BlueskyPublishManager: IPublishBlogManager {

    override suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules>? {
        if (account.platform.protocol.notBluesky) return null
        return Result.success(
            PublishBlogRules(
                maxCharacters = 300,
                maxMediaCount = 4,
                maxPollOptions = 0,
                supportSpoiler = false,
                supportPoll = false,
                maxLanguageCount = 2,
            )
        )
    }
}
