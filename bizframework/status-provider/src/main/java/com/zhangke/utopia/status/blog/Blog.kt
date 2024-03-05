package com.zhangke.utopia.status.blog

import com.zhangke.framework.serialize.DateSerializer
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.platform.BlogPlatform
import kotlinx.serialization.Serializable
import java.util.Date

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
    val emojis: List<Emoji>,
    val mentions: List<Mention>,
    val poll: BlogPoll?,
)
