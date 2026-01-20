package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.LocalNavBackStack
import kotlinx.coroutines.flow.Flow

@Composable
fun ConsumeOpenScreenFlow(
    openScreenFlow: Flow<NavKey>,
    backStack: NavBackStack<NavKey> = LocalNavBackStack.currentOrThrow,
) {
    ConsumeFlow(openScreenFlow) {
        val lastItem = backStack.lastOrNull()
        if (lastItem != it) {
            backStack.add(it)
        }
    }
}
