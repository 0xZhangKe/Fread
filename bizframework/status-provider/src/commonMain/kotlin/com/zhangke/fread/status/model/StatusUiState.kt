package com.zhangke.fread.status.model

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.author.updateFollowingState
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.status.model.Status
import kotlinx.serialization.Serializable

@Serializable
data class StatusUiState(
    val status: Status,
    val logged: Boolean,
    val isOwner: Boolean,
    val blogTranslationState: BlogTranslationUiState,
    val locator: PlatformLocator,
) : PlatformSerializable

@Serializable
data class BlogTranslationUiState(
    val support: Boolean,
    val translating: Boolean = false,
    val showingTranslation: Boolean = false,
    val blogTranslation: BlogTranslation? = null,
) : PlatformSerializable {

    companion object {
        val DEFAULT = BlogTranslationUiState(support = false)
    }
}

fun StatusUiState.updateBlogAuthor(block: (BlogAuthor) -> BlogAuthor): StatusUiState {
    val newStatus = when (status) {
        is Status.NewBlog -> {
            status.copy(
                blog = status.blog.copy(
                    author = block(status.blog.author)
                )
            )
        }

        is Status.Reblog -> {
            status.copy(
                reblog = status.reblog.copy(author = block(status.reblog.author))
            )
        }
    }
    return copy(status = newStatus)
}

fun StatusUiState.updateFollowingState(following: Boolean): StatusUiState {
    return updateBlogAuthor {
        it.updateFollowingState(following)
    }
}

fun List<StatusUiState>.updateStatus(
    status: StatusUiState,
): List<StatusUiState> {
    return map {
        if (it.status.intrinsicBlog.id == status.status.intrinsicBlog.id) {
            status
        } else {
            it
        }
    }
}
