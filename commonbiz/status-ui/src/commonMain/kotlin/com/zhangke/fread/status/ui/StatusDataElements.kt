package com.zhangke.fread.status.ui

import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.status.model.StatusActionType

/**
 * Status 本身的事件上报尽可能贴近触发该事件的位置
 */
object StatusDataElements {

    const val LIKE = "statusLike"
    const val DISLIKE = "statusDislike"
    const val COMMENT = "statusComment"
    const val FORWARD = "statusForward"
    const val UN_FORWARD = "statusUnForward"
    const val SHARE = "statusShare"
    const val MORE = "statusMore"
    const val BOOKMARK = "statusBookmark"
    const val UN_BOOKMARK = "statusUnBookmark"
    const val DELETE = "statusDelete"

    const val USER_INFO = "statusUserInfo"
    const val MEDIA = "statusMedia"
    const val VOTE = "statusVote"
    const val HASHTAG = "statusHashtag"
    const val MENTION = "statusMention"
    const val CONTENT = "statusContent"

    const val OPEN_IN_BROWSER = "statusOpenInBrowser"
    const val COPY_BLOG_LINK = "statusCopyBlogLink"
    const val TRANSLATE = "statusTranslate"
}

fun reportStatusInteractionClickEvent(actionType: StatusActionType) {
    when (actionType) {
        StatusActionType.LIKE -> reportClick(StatusDataElements.LIKE)
        StatusActionType.FORWARD -> reportClick(StatusDataElements.FORWARD)
        StatusActionType.BOOKMARK -> reportClick(StatusDataElements.BOOKMARK)
        StatusActionType.REPLY -> reportClick(StatusDataElements.COMMENT)
        StatusActionType.DELETE -> reportClick(StatusDataElements.DELETE)
        StatusActionType.SHARE -> reportClick(StatusDataElements.SHARE)
        StatusActionType.PIN -> reportClick(StatusDataElements.MORE)
        StatusActionType.EDIT -> reportClick(StatusDataElements.MORE)
    }
}
