package com.zhangke.fread.analytics

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab

internal actual val Screen.pageEventName: String
    get() = TODO("Not yet implemented")

internal actual val PagerTab.pageEventName: String
    get() = TODO("Not yet implemented")

internal actual fun Screen?.generateEventId(): String {
    if (this == null) return "unknownPage"
    TODO("Not yet implemented")
}

internal actual fun PagerTab?.generateEventId(): String {
    if (this == null) return "unknownPage"
    TODO("Not yet implemented")
}