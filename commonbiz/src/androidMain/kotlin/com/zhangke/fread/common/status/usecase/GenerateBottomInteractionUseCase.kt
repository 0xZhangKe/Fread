package com.zhangke.fread.common.status.usecase

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.status.status.model.StatusInteraction
import javax.inject.Inject

class GenerateBottomInteractionUseCase @Inject constructor() {

    operator fun invoke(interactions: List<StatusInteraction>): List<StatusUiInteraction> {
        return listOf(
            obtainCommentInteraction(interactions),
            obtainForwardInteraction(interactions),
            obtainLikeInteraction(interactions),
            StatusUiInteraction.Share,
        )
    }

    private fun obtainCommentInteraction(interactions: List<StatusInteraction>): StatusUiInteraction {
        var commentInteraction = interactions.mapFirstOrNull { it as? StatusInteraction.Comment }
        if (commentInteraction == null) {
            commentInteraction = StatusInteraction.Comment(
                commentCount = 0,
                enable = false,
            )
        }
        return StatusUiInteraction.Comment(commentInteraction)
    }

    private fun obtainForwardInteraction(interactions: List<StatusInteraction>): StatusUiInteraction {
        var forwardInteraction = interactions.mapFirstOrNull { it as? StatusInteraction.Forward }
        if (forwardInteraction == null) {
            forwardInteraction = StatusInteraction.Forward(
                forwardCount = 0,
                forwarded = false,
                enable = false,
            )
        }
        return StatusUiInteraction.Forward(forwardInteraction)
    }

    private fun obtainLikeInteraction(interactions: List<StatusInteraction>): StatusUiInteraction {
        var likeInteraction = interactions.mapFirstOrNull { it as? StatusInteraction.Like }
        if (likeInteraction == null) {
            likeInteraction = StatusInteraction.Like(
                likeCount = 0,
                liked = false,
                enable = false,
            )
        }
        return StatusUiInteraction.Like(likeInteraction)
    }
}
