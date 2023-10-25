package com.zhangke.framework.composable.inline

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalPlayableIndexRecorder: ProvidableCompositionLocal<PlayableIndexRecorder?> =
    staticCompositionLocalOf { null }

class PlayableIndexRecorder {

    private val _recorder = sortedSetOf<Int>()

    fun getIntervalIndexList(startIndex: Int, endIndex: Int): List<Int> {
        val intervalList = mutableListOf<Int>()
        _recorder.forEach { index ->
            if (index > endIndex) return@forEach
            if (index in startIndex..endIndex) {
                intervalList += index
            }
        }
        return intervalList
    }

    fun recordePlayableIndex(index: Int) {
        _recorder += index
    }

    fun removePlayableIndex(index: Int) {
        _recorder -= index
    }
}
