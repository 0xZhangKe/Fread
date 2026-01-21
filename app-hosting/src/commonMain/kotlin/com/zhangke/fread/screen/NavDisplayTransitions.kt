package com.zhangke.fread.screen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent.SwipeEdge

private const val DEFAULT_TRANSITION_DURATION_MILLISECOND = 700

fun <T : Any> freadTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform =
    {
        ContentTransform(
            targetContentEnter = defaultFadeIn(),
            initialContentExit = defaultFadeOut(),
        )
    }

fun <T : Any> freadPopTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform =
    {
        ContentTransform(
            targetContentEnter = defaultFadeIn(),
            initialContentExit = defaultFadeOut(),
        )
    }

fun <T : Any> freadPredictivePopTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.(@SwipeEdge Int) -> ContentTransform =
    {
        ContentTransform(
            targetContentEnter = defaultFadeIn(),
            initialContentExit = scaleOut(targetScale = 0.85F) + fadeOut(targetAlpha = 0.9F),
        )
    }

private fun defaultFadeIn(): EnterTransition = fadeIn(
    animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)
)

private fun defaultFadeOut(): ExitTransition = fadeOut(
    animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)
)
