package com.zhangke.framework.voyager

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CurrentAnimatedScreen(navigator: Navigator) {
    val currentScreen = navigator.lastItem
    AnimatedContent(
        targetState = currentScreen.key,
        transitionSpec = {
            (fadeIn(animationSpec = tween(220)) +
                    scaleIn(initialScale = 0.92f, animationSpec = tween(220)))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        },
    ) { targetScreenKey ->
        val targetScreen = navigator.items.lastOrNull { it.key == targetScreenKey }
        if (targetScreen != null) {
            navigator.saveableState("currentScreen", screen = targetScreen) {
                val scope = AnimatedScreenContentScope(
                    animatedContentScope = this,
                    sharedTransitionScope = this@CurrentAnimatedScreen,
                )
                CurrentContent(targetScreen, scope)
            }
        }
    }
}

@Composable
fun CurrentContent(
    screen: Screen,
    animatedScreenContentScope: AnimatedScreenContentScope,
) {
    if (screen is AnimatedScreen) {
        with(screen) { AnimationContent(animatedScreenContentScope) }
    } else {
        with(screen) { Content() }
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedElementBetweenScreen(
    animatedScreenContentScope: AnimatedScreenContentScope?,
    key: String?,
): Modifier {
    if (key.isNullOrEmpty()) return this
    if (animatedScreenContentScope == null) return this
    return with(animatedScreenContentScope.sharedTransitionScope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = animatedScreenContentScope.animatedContentScope
        )
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedBoundsBetweenScreen(
    animatedScreenContentScope: AnimatedScreenContentScope?,
    key: String?,
): Modifier {
    if (key.isNullOrEmpty()) return this
    if (animatedScreenContentScope == null) return this
    return with(animatedScreenContentScope.sharedTransitionScope) {
        sharedBounds(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = animatedScreenContentScope.animatedContentScope
        )
    }
}
