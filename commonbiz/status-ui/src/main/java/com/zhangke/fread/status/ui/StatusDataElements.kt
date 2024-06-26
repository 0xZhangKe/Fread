package com.zhangke.fread.status.ui

import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.status.status.model.StatusInteraction

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
}

fun reportStatusInteractionClickEvent(interaction: StatusUiInteraction) {
    if (interaction is StatusUiInteraction.Share) {
        reportClick(StatusDataElements.SHARE)
        return
    }
    val element = when (val statusInteraction = interaction.statusInteraction) {
        is StatusInteraction.Delete -> StatusDataElements.DELETE
        is StatusInteraction.Like -> {
            if (statusInteraction.liked) StatusDataElements.DISLIKE else StatusDataElements.LIKE
        }

        is StatusInteraction.Comment -> StatusDataElements.COMMENT
        is StatusInteraction.Forward -> {
            if (statusInteraction.forwarded) StatusDataElements.UN_FORWARD else StatusDataElements.FORWARD
        }

        is StatusInteraction.Bookmark -> {
            if (statusInteraction.bookmarked) StatusDataElements.UN_BOOKMARK else StatusDataElements.BOOKMARK
        }

        else -> return
    }
    reportClick(element)
}
