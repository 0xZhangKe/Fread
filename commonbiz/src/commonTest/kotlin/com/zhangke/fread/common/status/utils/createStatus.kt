package com.zhangke.fread.common.status.utils

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.common.utils.createActivityPubUserUri
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.PostingApplication
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun createStatus(
    id: String = "1",
    title: String = "title",
    author: BlogAuthor = createBlogAuthor(),
    content: String = "content",
    date: Instant = Clock.System.now(),
    forwardCount: Int? = null,
    likeCount: Int? = null,
    repliesCount: Int? = null,
    sensitive: Boolean = false,
    spoilerText: String = "",
    mediaList: List<BlogMedia> = emptyList(),
    pinned: Boolean = false,
    poll: BlogPoll? = null,
    visibility: StatusVisibility = StatusVisibility.PUBLIC,
    card: PreviewCard? = null,
    isSelf: Boolean = false,
    supportTranslate: Boolean = false,
    editedAt: Instant? = null,
    application: PostingApplication? = null,
): Status {
    return Status.NewBlog(
        blog = Blog(
            id = id,
            author = author,
            description = null,
            content = content,
            title = title,
            date = com.zhangke.framework.datetime.Instant(date),
            emojis = emptyList(),
            forwardCount = forwardCount,
            likeCount = likeCount,
            repliesCount = repliesCount,
            url = "",
            sensitive = sensitive,
            spoilerText = spoilerText,
            mediaList = mediaList,
            poll = poll,
            platform = createBlogPlatform(),
            mentions = emptyList(),
            tags = emptyList(),
            card = null,
            supportTranslate = false,
            pinned = pinned,
            visibility = visibility,
            isSelf = isSelf,
            editedAt = null,
            application = application,
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
    emojis = emptyList(),
)
