package com.zhangke.framework.voyager

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CurrentAnimatedScreen(navigator: Navigator) {
    val currentScreen = navigator.lastItem
    AnimatedContent(
        targetState = currentScreen.key,
    ) { targetScreenKey ->
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this
        ) {
            val targetScreen = navigator.items.lastOrNull { it.key == targetScreenKey }
            if (targetScreen != null) {
                navigator.saveableState("currentScreen", screen = targetScreen) {
                    targetScreen.Content()
                }
            }

//            val currentScreenKey = if (currentScreen != targetScreen) {
//                "destroyingScreen"
//            } else {
//                "currentScreen"
//            }
//            navigator.saveableState(currentScreenKey, screen = currentScreen) {
//                currentScreen.Content()
//            }
//            if (targetScreen != null && currentScreen != targetScreen) {
//                navigator.saveableState("targetScreen", screen = targetScreen) {
//                    targetScreen.Content()
//                }
//            }
        }
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedBoundsBetweenScreen(key: String?): Modifier {
    if (key.isNullOrEmpty()) return this
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
    val sharedTransitionScope = LocalSharedTransitionScope.current
    if (animatedVisibilityScope == null || sharedTransitionScope == null) return this
    return with(sharedTransitionScope) {
        sharedBounds(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = animatedVisibilityScope,
        )
    }
}
