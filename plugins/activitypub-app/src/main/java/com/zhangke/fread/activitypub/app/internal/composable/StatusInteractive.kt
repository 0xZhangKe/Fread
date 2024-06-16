package com.zhangke.fread.activitypub.app.internal.composable

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.pushDestination
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.common.status.model.StatusUiState

@Composable
fun statusInteractive(
    statusUiState: StatusUiState,
    onInteractive: (StatusUiState, StatusUiInteraction) -> Unit,
): (StatusUiInteraction) -> Unit {
    val navigator = LocalNavigator.currentOrThrow
    return { interaction ->
        if (interaction is StatusUiInteraction.Comment) {
            navigator.pushDestination(
                PostStatusScreenRoute.buildRoute(
                    accountUri = statusUiState.status.intrinsicBlog.author.uri,
                    replyToBlogId = statusUiState.status.id,
                    replyAuthorName = statusUiState.status.intrinsicBlog.author.name,
                )
            )
        } else {
            onInteractive(statusUiState, interaction)
        }
    }
}
