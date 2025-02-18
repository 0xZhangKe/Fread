package com.zhangke.fread.status.publish

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules

/**
 * 最上面的输入帖子内容区域包含基础的帖子内容，比如文本和图片。
 * 下面紧跟着选择发帖用户。
 * 再往下面是用户发帖预览列表视图，如果选中了三个账号，那么会有三个帖文预览View，其中内容跟着最上面的基础帖文部分变化。
 * 没个账号对应的预览部分可以独立修改，不影响其他内容，但是基础部分的修改会同步到所有账号。没个账号的帖文区域也包含特色功能。
 */
class PublishBlogManager(
    private val managerList: List<IPublishBlogManager>,
) {

}

interface IPublishBlogManager {

    suspend fun getPublishBlogRules(account: LoggedAccount): Result<PublishBlogRules>

}
