package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

class GlobalScreenProvider {

    var content = mutableStateOf<(@Composable () -> Unit)?>(null)

}

val LocalGlobalScreenProvider = compositionLocalOf {
    GlobalScreenProvider()
}
