package com.zhangke.utopia.common.status.repo.db

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.common.utils.createActivityPubUserUri
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.model.StatusType
import com.zhangke.utopia.status.uri.FormalUri

fun createStatusContentEntity(
    id: String = "1",
    nextStatusId: String? = null,
    authorUri: FormalUri = createActivityPubUserUri(),
    authorWebFinger: WebFinger = WebFinger.create("@AtomZ@m.cmx.im")!!,
    authorName: String = "Atom",
    authorDescription: String = "Android dev for Utopia.",
    authorAvatar: String? = "mocked avatar",
    sourceUri: FormalUri = createActivityPubUserUri(),
    type: StatusType = StatusType.BLOG,
    statusIdOfPlatform: String = "42",
    title: String? = "mocked title",
    content: String = "mocked content",
    createTimestamp: Long = System.currentTimeMillis(),
    forwardCount: Int? = 100,
    likeCount: Int? = 1000,
    repliesCount: Int? = 2000,
    sensitive: Boolean = false,
    spoilerText: String = "mocked spoiler text",
    mediaList: List<BlogMedia> = emptyList(),
    poll: BlogPoll? = null,
) = StatusContentEntity(
    id = id,
    nextStatusId = nextStatusId,
    authorUri = authorUri,
    authorWebFinger = authorWebFinger,
    authorName = authorName,
    authorDescription = authorDescription,
    authorAvatar = authorAvatar,
    sourceUri = sourceUri,
    type = type,
    statusIdOfPlatform = statusIdOfPlatform,
    title = title,
    content = content,
    createTimestamp = createTimestamp,
    forwardCount = forwardCount,
    likeCount = likeCount,
    repliesCount = repliesCount,
    sensitive = sensitive,
    spoilerText = spoilerText,
    mediaList = mediaList,
    poll = poll,
)
