package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.Flow

@Composable
fun ConsumeOpenScreenFlow(
    openScreenFlow: Flow<Screen>,
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    ConsumeFlow(openScreenFlow) {
        val lastItem = navigator.items.lastOrNull()
        if (lastItem != it) {
            navigator.push(it)
        }
    }
}
