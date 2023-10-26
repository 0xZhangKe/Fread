package com.zhangke.framework.composable.inline

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.framework.composable.sensitive.SensitiveLazyColumnState
import com.zhangke.framework.ktx.isSingle
import com.zhangke.framework.ktx.second
import kotlin.math.max

val LocalPlayableIndexRecorder: ProvidableCompositionLocal<PlayableIndexRecorder?> =
    staticCompositionLocalOf { null }

class PlayableIndexRecorder {

    private val _recorder = sortedSetOf<Int>()

    fun getCenterIndex(state: SensitiveLazyColumnState, playablePercentThreshold: Float): Int? {
        val indexList = getIntervalIndexList(state.firstVisibleIndex, state.lastVisibleIndex)
        if (indexList.isEmpty()) return null
        if (indexList.isSingle()) {
            return state.getValidateIndexOrNull(indexList.first(), playablePercentThreshold)
        }
        if (indexList.size == 2) {
            val maxIndex = max(indexList.first(), indexList.second())
            return state.getValidateIndexOrNull(maxIndex, playablePercentThreshold)
        }
        val centerIndex = indexList[indexList.size / 2]
        return state.getValidateIndexOrNull(centerIndex, playablePercentThreshold)
    }

    private fun SensitiveLazyColumnState.getValidateIndexOrNull(
        index: Int,
        playablePercentThreshold: Float,
    ): Int? {
        val percent = getVisiblePercentOfIndex(index)
        if (percent >= playablePercentThreshold) return index
        return null
    }

    private fun getIntervalIndexList(startIndex: Int, endIndex: Int): List<Int> {
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
