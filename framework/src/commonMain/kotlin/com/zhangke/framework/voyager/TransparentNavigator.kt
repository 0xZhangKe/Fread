package com.zhangke.framework.voyager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.Stack
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.framework.composable.noRippleClick

typealias TransparentNavigatorContent =
        @Composable (transparentNavigator: TransparentNavigator) -> Unit

val LocalTransparentNavigator: ProvidableCompositionLocal<TransparentNavigator> =
    staticCompositionLocalOf { error("TransparentNavigator not initialized") }

@OptIn(InternalVoyagerApi::class)
@Composable
fun TransparentNavigator(
    key: String = currentCompositeKeyHash.toString(35),
    transparentContent: TransparentNavigatorContent = { CurrentScreen() },
    content: TransparentNavigatorContent
) {
    Navigator(HiddenTransparentScreen, onBackPressed = null, key = key) { navigator ->
        val transparentNavigator = remember(navigator) {
            TransparentNavigator(navigator)
        }

        CompositionLocalProvider(
            LocalTransparentNavigator provides transparentNavigator
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content(transparentNavigator)
                val lastItem = transparentNavigator.lastItemOrNull
                if (lastItem != null) {
                    BackHandler(true) {
                        transparentNavigator.pop()
                    }
                    Box(
                        modifier = Modifier
                            .noRippleClick {}
                    ) {
                        transparentContent(transparentNavigator)
                    }
                }
            }
        }
    }
}

class TransparentNavigator internal constructor(
    private val navigator: Navigator,
) : Stack<Screen> by navigator

private object HiddenTransparentScreen : Screen {

    @Composable
    override fun Content() {
        Spacer(modifier = Modifier.height(1.dp))
    }
}
