package com.zhangke.framework.voyager

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.compositionUniqueId

@OptIn(ExperimentalSharedTransitionApi::class, InternalVoyagerApi::class)
@Composable
fun SharedElementNavigator(
    screen: Screen,
    key: String = compositionUniqueId(),
) {
    SharedTransitionLayout {
        Navigator(
            screen = screen,
            key = key,
            content = { navigator ->
                CurrentAnimatedScreen(navigator)
            },
        )
    }
}
