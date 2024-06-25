package com.zhangke.fread.analytics

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun ObserveScreenForAnalytics(navigator: Navigator) {
    val pageIdToExposed = remember(navigator) {
        mutableMapOf<String, String>()
    }
    val lastItem = navigator.lastItemOrNull
    val eventId = lastItem?.generateEventId().orEmpty()
    LaunchedEffect(lastItem) {
        Log.d("F_TEST", "last item is $eventId")
        if (pageIdToExposed[eventId] == null) {
            pageIdToExposed[eventId] = eventId
            lastItem?.let { onPageShow(it) }
            Log.d("F_TEST", "expose page $eventId")
        }
    }
}

private fun onPageShow(screen: Screen) {
    reportPageShow(screen.pageEventName)
}

private val Screen.pageEventName: String
    get() = this::class.java.simpleName

private fun Screen?.generateEventId(): String {
    if (this == null) return "unknownPage"
    return "${this.javaClass.simpleName}@${this.hashCode()}"
}
