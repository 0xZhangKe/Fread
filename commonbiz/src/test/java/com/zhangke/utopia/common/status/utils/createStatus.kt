package com.zhangke.utopia.common.status.utils

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.common.utils.createActivityPubUserUri
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import java.util.Date

fun createStatus(
    id: String = "1",
    title: String = "title",
    author: BlogAuthor = createBlogAuthor(),
    content: String = "content",
    date: Date = Date(),
    forwardCount: Int? = null,
    likeCount: Int? = null,
    repliesCount: Int? = null,
    sensitive: Boolean = false,
    spoilerText: String = "",
    mediaList: List<BlogMedia> = emptyList(),
    poll: BlogPoll? = null,
): Status {
    return Status.NewBlog(
        blog = Blog(
            id = id,
            author = author,
            content = content,
            title = title,
            date = date,
            forwardCount = forwardCount,
            likeCount = likeCount,
            repliesCount = repliesCount,
            sensitive = sensitive,
            spoilerText = spoilerText,
            mediaList = mediaList,
            poll = poll,
        )
    )
}

fun createBlogAuthor(
    uri: FormalUri = createActivityPubUserUri(),
    webFinger: WebFinger = WebFinger.create("@AtomZ@m.cmx.im")!!,
    name: String = "Atom",
    description: String = "mock desc",
    avatar: String? = null,
) = BlogAuthor(
    uri = uri,
    webFinger = webFinger,
    name = name,
    description = description,
    avatar = avatar,
)
