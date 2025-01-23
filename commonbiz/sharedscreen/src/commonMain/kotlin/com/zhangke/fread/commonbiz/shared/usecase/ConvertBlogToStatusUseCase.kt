package com.zhangke.fread.commonbiz.shared.usecase

import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import me.tatarka.inject.annotations.Inject

class ConvertNewBlogToStatusUseCase @Inject constructor() {

    operator fun invoke(blog: Blog): Status {
        val supportActions = getStatusInteraction(
            blog = blog,
            isSelfStatus = blog.isSelf,
            logged = true,
        )
        return Status.NewBlog(blog, supportActions)
    }

    private fun getStatusInteraction(
        blog: Blog,
        isSelfStatus: Boolean,
        logged: Boolean,
    ): List<StatusInteraction> {
        val actionList = mutableListOf<StatusInteraction>()
        actionList += StatusInteraction.Like(
            likeCount = blog.likeCount.toInt(),
            liked = blog.liked,
            enable = logged,
        )
        actionList += StatusInteraction.Forward(
            forwardCount = blog.forwardCount.toInt(),
            forwarded = blog.forward,
            enable = logged,
        )
        actionList += StatusInteraction.Comment(
            commentCount = blog.repliesCount.toInt(),
            enable = logged,
        )
        actionList += StatusInteraction.Bookmark(
            bookmarkCount = null,
            bookmarked = blog.bookmarked,
            enable = logged,
        )
        if (isSelfStatus) {
            actionList.add(StatusInteraction.Delete(enable = true))
            actionList.add(StatusInteraction.Pin(pinned = blog.pinned, enable = true))
            actionList.add(StatusInteraction.Edit(true))
        }
        return actionList
    }

    private fun Long?.toInt(): Int {
        return this?.toInt() ?: 0
    }
}
