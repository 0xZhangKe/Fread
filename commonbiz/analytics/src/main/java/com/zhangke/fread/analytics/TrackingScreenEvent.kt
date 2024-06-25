package com.zhangke.fread.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen

//@Composable
//fun ObserveNavigatorForAnalytics(navigator: Navigator) {
//    val exposedPageIdSet = remember(navigator) {
//        mutableSetOf<String>()
//    }
//    val lastItem = navigator.lastItemOrNull
//    if (lastItem != null) {
//        val eventId = lastItem.generateEventId()
//        Log.d("DataTracking", "last item is $eventId")
//        if (!exposedPageIdSet.contains(eventId)) {
//            LaunchedEffect(lastItem) {
//                exposedPageIdSet.add(eventId)
//                onPageShow(lastItem)
//            }
//        }
//    }
//}

@Composable
fun Screen.TrackingScreenEvent(paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {}) {
    var pageShown by rememberSaveable {
        mutableStateOf(false)
    }
    if (!pageShown) {
        LaunchedEffect(this) {
            onPageShow(this@TrackingScreenEvent, paramsBuilder)
        }
        pageShown = true
    }
}

private fun onPageShow(
    screen: Screen,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit),
) {
    reportPageShow(screen.pageEventName, paramsBuilder)
}

private val Screen.pageEventName: String
    get() = this::class.java.simpleName

private fun Screen?.generateEventId(): String {
    if (this == null) return "unknownPage"
    return "${this.javaClass.simpleName}@${this.hashCode()}"
}
