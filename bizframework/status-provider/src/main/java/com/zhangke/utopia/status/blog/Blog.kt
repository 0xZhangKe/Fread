package com.zhangke.utopia.status.blog

import com.zhangke.framework.serialize.DateSerializer
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.platform.BlogPlatform
import kotlinx.serialization.Serializable
import java.util.Date

/**
 * 不要轻易修改他的结构，这个结构是会被序列化存入本地数据库的。
 * 其中的子结构同理。
 * @see [StatusContentTable]
 */
@Serializable
data class Blog(
    val id: String,
    val author: BlogAuthor,
    val title: String?,
    val content: String,
    @Serializable(with = DateSerializer::class) val date: Date,
    val forwardCount: Int?,
    val likeCount: Int?,
    val repliesCount: Int?,
    val sensitive: Boolean,
    val spoilerText: String,
    val platform: BlogPlatform,
    val mediaList: List<BlogMedia>,
    val poll: BlogPoll?,
)
