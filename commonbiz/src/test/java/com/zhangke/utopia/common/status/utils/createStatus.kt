package com.zhangke.utopia.common.status.utils

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.common.utils.createActivityPubUserUri
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.StatusProviderProtocol
import com.zhangke.utopia.status.platform.BlogPlatform
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
            emojis = emptyList(),
            forwardCount = forwardCount,
            likeCount = likeCount,
            repliesCount = repliesCount,
            sensitive = sensitive,
            spoilerText = spoilerText,
            mediaList = mediaList,
            poll = poll,
            platform = createBlogPlatform(),
            mentions = emptyList(),
        ),
        supportInteraction = emptyList()
    )
}

fun createBlogPlatform(
    uri: String = "https://example.com",
    name: String = "example",
    description: String = "description",
    baseUrl: FormalBaseUrl = FormalBaseUrl.build("https", "example.com"),
    protocol: StatusProviderProtocol = mockStatusProviderProtocol(),
    thumbnail: String? = null,
): BlogPlatform {
    return BlogPlatform(
        uri = uri,
        name = name,
        description = description,
        baseUrl = baseUrl,
        protocol = protocol,
        thumbnail = thumbnail
    )
}

fun mockStatusProviderProtocol(): StatusProviderProtocol {
    return StatusProviderProtocol(
        id = "id",
        name = "example",
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
