package com.zhangke.framework.voyager

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import cafe.adriel.voyager.transitions.ScreenTransitionContent

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun FreadScreenTransition(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    disposeScreenAfterTransitionEnd: Boolean,
    content: ScreenTransitionContent = { it.Content() }
) {
    ScreenTransition(
        navigator = navigator,
        modifier = modifier,
        content = content,
        disposeScreenAfterTransitionEnd = disposeScreenAfterTransitionEnd,
        transition = {
            (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                    scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        },
    )
}
