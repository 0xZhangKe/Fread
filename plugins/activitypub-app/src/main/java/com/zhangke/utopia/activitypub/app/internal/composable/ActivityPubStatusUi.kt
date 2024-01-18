package com.zhangke.utopia.activitypub.app.internal.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.pushDestination
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.status.status.model.Status

@Composable
fun ActivityPubStatusUi(
    modifier: Modifier = Modifier,
    status: Status,
    indexInList: Int,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    onInteractive: (Status, StatusUiInteraction) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    FeedsStatusNode(
        modifier = modifier,
        status = status,
        indexInList = indexInList,
        bottomPanelInteractions = bottomPanelInteractions,
        moreInteractions = moreInteractions,
        onInteractive = { _, interaction ->
            if (interaction is StatusUiInteraction.Comment) {
                navigator.pushDestination(
                    PostStatusScreenRoute.buildRoute(
                        replyToBlogId = status.id,
                        replyAuthorName = status.intrinsicBlog.author.name,
                    )
                )
            } else {
                onInteractive(status, interaction)
            }
        },
    )
}
