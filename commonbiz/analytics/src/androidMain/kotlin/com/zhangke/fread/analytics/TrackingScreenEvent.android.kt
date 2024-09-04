package com.zhangke.fread.analytics

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab

internal actual val Screen.pageEventName: String
    get() = this::class.java.simpleName

internal actual val PagerTab.pageEventName: String
    get() = this::class.java.simpleName

internal actual fun Screen?.generateEventId(): String {
    if (this == null) return "unknownPage"
    return "${this.javaClass.simpleName}@${this.hashCode()}"
}

internal actual fun PagerTab?.generateEventId(): String {
    if (this == null) return "unknownPage"
    return "${this.javaClass.simpleName}@${this.hashCode()}"
}
