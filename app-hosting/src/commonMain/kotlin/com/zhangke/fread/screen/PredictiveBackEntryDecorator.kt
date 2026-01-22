package com.zhangke.fread.screen

import androidx.compose.animation.EnterExitState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationEventState
import com.zhangke.framework.architect.theme.dialogScrim
import com.zhangke.framework.nav.FREAD_DIALOG_METADATA_KEY

@Immutable
data class PredictiveBackState(
    val inProgress: Boolean = false,
    val progress: Float = 0f,
    @param:NavigationEvent.SwipeEdge val swipeEdge: Int = NavigationEvent.EDGE_NONE,
)

val LocalPredictiveBackState = compositionLocalOf { PredictiveBackState() }

@Composable
fun <T : Any> rememberPredictiveBackEntryDecorator(): NavEntryDecorator<T> =
    remember { NavEntryDecorator { entry -> PredictiveBackDecoratedEntry(entry) } }

@Composable
fun rememberPredictiveBackState(
    navigationEventState: NavigationEventState<*>,
): PredictiveBackState {
    return when (val transitionState = navigationEventState.transitionState) {
        is NavigationEventTransitionState.InProgress -> {
            if (transitionState.direction == NavigationEventTransitionState.TRANSITIONING_BACK) {
                PredictiveBackState(
                    inProgress = true,
                    progress = transitionState.latestEvent.progress.coerceIn(0f, 1f),
                    swipeEdge = transitionState.latestEvent.swipeEdge,
                )
            } else {
                PredictiveBackState()
            }
        }

        else -> PredictiveBackState()
    }
}

@Composable
private fun <T : Any> PredictiveBackDecoratedEntry(entry: NavEntry<T>) {
    val isDialogEntry = entry.metadata[FREAD_DIALOG_METADATA_KEY] as? Boolean
    if (isDialogEntry == true) {
        entry.Content()
        return
    }
    val predictiveBackState = LocalPredictiveBackState.current
    val transition = LocalNavAnimatedContentScope.current.transition
    var wasPredictiveBack by remember { mutableStateOf(false) }
    var scrimActive by remember { mutableStateOf(false) }
    val isExiting = transition.targetState == EnterExitState.PostExit
    val isEntering =
        transition.targetState == EnterExitState.Visible && transition.currentState == EnterExitState.PreEnter
    if (predictiveBackState.inProgress) {
        wasPredictiveBack = true
    } else if (!transition.isRunning) {
        wasPredictiveBack = false
    }
    if (predictiveBackState.inProgress && isEntering) {
        scrimActive = true
    } else if (!predictiveBackState.inProgress) {
        scrimActive = false
    }
    val deviceCornerRadius = rememberDeviceCornerRadius()
    val clipRadius =
        if ((predictiveBackState.inProgress && isExiting) || (isExiting && wasPredictiveBack)) {
            deviceCornerRadius
        } else {
            0.dp
        }
    val scrimVisible = scrimActive
    val scrimColor =
        if (scrimVisible) {
            MaterialTheme.colorScheme.dialogScrim
        } else {
            Color.Transparent
        }
    val modifier = Modifier.then(
        if (clipRadius > 0.dp) {
            Modifier.clip(RoundedCornerShape(clipRadius))
        } else {
            Modifier
        }
    ).then(
        if (scrimColor.alpha > 0f) {
            Modifier.drawWithContent {
                drawContent()
                drawRect(scrimColor)
            }
        } else {
            Modifier
        }
    )
    Box(modifier = modifier) {
        entry.Content()
    }
}
