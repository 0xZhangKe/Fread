package com.zhangke.fread.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab

private val screenShownIdSet = mutableSetOf<String>()
private val tabShownIdSet = mutableSetOf<String>()

@Composable
fun Screen.TrackingScreenEvent(paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {}) {
    val eventId = this.generateEventId()
    if (!screenShownIdSet.contains(eventId)) {
        screenShownIdSet.add(eventId)
        LaunchedEffect(this) {
            onPageShow(this@TrackingScreenEvent, paramsBuilder)
        }
    }
    DisposableEffect(this) {
        onDispose { screenShownIdSet.remove(eventId) }
    }
}

@Composable
fun PagerTab.TrackingTabEvent(paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {}) {
    val eventId = this.generateEventId()
    if (!tabShownIdSet.contains(eventId)) {
        tabShownIdSet.add(eventId)
        LaunchedEffect(this) {
            onTabShow(this@TrackingTabEvent, paramsBuilder)
        }
    }
    DisposableEffect(this) {
        onDispose { tabShownIdSet.remove(eventId) }
    }
}

private fun onPageShow(
    screen: Screen,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit),
) {
    reportPageShow(screen.pageEventName, paramsBuilder)
}

private fun onTabShow(
    tab: PagerTab,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit),
) {
    reportPageShow(tab.pageEventName, paramsBuilder)
}

private val Screen.pageEventName: String
    get() = this::class.java.simpleName

private val PagerTab.pageEventName: String
    get() = this::class.java.simpleName

private fun Screen?.generateEventId(): String {
    if (this == null) return "unknownPage"
    return "${this.javaClass.simpleName}@${this.hashCode()}"
}

private fun PagerTab?.generateEventId(): String {
    if (this == null) return "unknownPage"
    return "${this.javaClass.simpleName}@${this.hashCode()}"
}
