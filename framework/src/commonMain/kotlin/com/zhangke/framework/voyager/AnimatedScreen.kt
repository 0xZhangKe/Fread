package com.zhangke.framework.voyager

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

abstract class AnimatedScreen : Screen {

    @Composable
    abstract fun AnimationContent(animatedScreenContentScope: AnimatedScreenContentScope)

    @Composable
    final override fun Content() {
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
class AnimatedScreenContentScope(
    val animatedContentScope: AnimatedContentScope,
    val sharedTransitionScope: SharedTransitionScope,
)
