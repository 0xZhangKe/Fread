package com.zhangke.fread.screen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEvent.SwipeEdge

private const val DEFAULT_TRANSITION_DURATION_MILLISECOND = 700
private const val PREDICTIVE_POP_END_DURATION_MILLISECOND = 1
private const val PREDICTIVE_POP_PIVOT_EDGE_FRACTION = 0.85f

fun <T : Any> freadTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform =
    {
        ContentTransform(
            targetContentEnter = defaultFadeIn() + scaleIn(initialScale = 0.6F),
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
    { swipeEdge ->
        val transformOrigin =
            when (swipeEdge) {
                NavigationEvent.EDGE_LEFT ->
                    TransformOrigin(PREDICTIVE_POP_PIVOT_EDGE_FRACTION, 0.5f)
                NavigationEvent.EDGE_RIGHT ->
                    TransformOrigin(1f - PREDICTIVE_POP_PIVOT_EDGE_FRACTION, 0.5f)
                else -> TransformOrigin.Center
            }
        ContentTransform(
            targetContentEnter = fadeIn(
                animationSpec = tween(
                    PREDICTIVE_POP_END_DURATION_MILLISECOND
                )
            ),
            initialContentExit = scaleOut(
                targetScale = 0.8F,
                animationSpec = tween(PREDICTIVE_POP_END_DURATION_MILLISECOND),
                transformOrigin = transformOrigin,
            ),
        )
    }

private fun defaultFadeIn(): EnterTransition = fadeIn(
    animationSpec = tween(durationMillis = DEFAULT_TRANSITION_DURATION_MILLISECOND)
)

private fun defaultFadeOut(): ExitTransition = fadeOut(
    animationSpec = tween(durationMillis = DEFAULT_TRANSITION_DURATION_MILLISECOND)
)
