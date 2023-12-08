package com.zhangke.utopia.common.status.adapter

import com.zhangke.utopia.common.status.repo.db.StatusContentEntity
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusType
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class StatusContentEntityAdapter @Inject constructor() {

    fun toStatus(entity: StatusContentEntity): Status {
        return Status.NewBlog(toBlog(entity))
    }

    fun toEntity(sourceUri: StatusProviderUri, status: Status): StatusContentEntity {
        val blog = (status as Status.NewBlog).blog
        return StatusContentEntity(
            id = 0,
            sourceUri = sourceUri,
            type = StatusType.BLOG,
            statusId = blog.id,
            authorUri = blog.author.uri,
            authorWebFinger = blog.author.webFinger,
            authorName = blog.author.name,
            authorDescription = blog.author.description,
            authorAvatar = blog.author.avatar,
            title = blog.title,
            content = blog.content,
            date = blog.date,
            forwardCount = blog.forwardCount,
            likeCount = blog.likeCount,
            repliesCount = blog.repliesCount,
            sensitive = blog.sensitive,
            spoilerText = blog.spoilerText,
            mediaList = blog.mediaList,
            poll = blog.poll,
        )
    }

    fun toBlog(entity: StatusContentEntity): Blog {
        return Blog(
            id = entity.statusId,
            author = toAuthor(entity),
            title = entity.title,
            content = entity.content,
            date = entity.date,
            forwardCount = entity.forwardCount,
            likeCount = entity.likeCount,
            repliesCount = entity.repliesCount,
            sensitive = entity.sensitive,
            spoilerText = entity.spoilerText,
            mediaList = entity.mediaList,
            poll = entity.poll
        )
    }

    fun toAuthor(entity: StatusContentEntity): BlogAuthor {
        return BlogAuthor(
            uri = entity.authorUri,
            webFinger = entity.authorWebFinger,
            name = entity.authorName,
            description = entity.authorDescription,
            avatar = entity.authorAvatar,
        )
    }
}
