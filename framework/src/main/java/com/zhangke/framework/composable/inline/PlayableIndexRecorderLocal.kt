package com.zhangke.framework.composable.inline

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.framework.composable.sensitive.SensitiveLazyColumnState
import com.zhangke.framework.ktx.isSingle
import com.zhangke.framework.ktx.second
import kotlin.math.max

val LocalPlayableIndexRecorder: ProvidableCompositionLocal<PlayableIndexRecorder?> =
    staticCompositionLocalOf { null }

class PlayableIndexRecorder {

    companion object {

        private const val PLAYABLE_PERCENT_THRESHOLD = 0.3F
        private const val UNSPECIFIED_INDEX = -1
    }

    private val _recorder = sortedSetOf<Int>()

    private val _currentActiveIndex = mutableIntStateOf(-1)
    val currentActiveIndex: Int by _currentActiveIndex

    fun updateLayoutState(state: SensitiveLazyColumnState) {
        val threshold = PLAYABLE_PERCENT_THRESHOLD
        val currentActiveInlineIndex = _currentActiveIndex.intValue
        if (currentActiveInlineIndex >= 0) {
            val currentActivePlayablePercent =
                state.getVisiblePercentOfIndex(currentActiveInlineIndex)
            if (currentActivePlayablePercent >= threshold) return
        }
        _currentActiveIndex.intValue = getCenterIndex(state) ?: UNSPECIFIED_INDEX
    }

    fun changeActiveIndex(index: Int) {
        _currentActiveIndex.intValue = index
    }

    private fun getCenterIndex(state: SensitiveLazyColumnState): Int? {
        val indexList = getIntervalIndexList(state.firstVisibleIndex, state.lastVisibleIndex)
        if (indexList.isEmpty()) return null
        if (indexList.isSingle()) {
            return state.getValidateIndexOrNull(indexList.first(), PLAYABLE_PERCENT_THRESHOLD)
        }
        if (indexList.size == 2) {
            val maxIndex = max(indexList.first(), indexList.second())
            return state.getValidateIndexOrNull(maxIndex, PLAYABLE_PERCENT_THRESHOLD)
        }
        val centerIndex = indexList[indexList.size / 2]
        return state.getValidateIndexOrNull(centerIndex, PLAYABLE_PERCENT_THRESHOLD)
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
