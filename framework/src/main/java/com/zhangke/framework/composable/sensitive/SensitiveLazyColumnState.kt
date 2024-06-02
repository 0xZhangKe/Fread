package com.zhangke.framework.composable.sensitive

data class SensitiveLazyColumnState(
    val firstVisibleIndex: Int,
    val firstVisiblePercent: Float,
    val lastVisibleIndex: Int,
    val lastVisiblePercent: Float,
    val isScrollInProgress: Boolean,
) {

    fun getVisiblePercentOfIndex(index: Int): Float {
        if (index == firstVisibleIndex) return firstVisiblePercent
        if (index == lastVisibleIndex) return lastVisiblePercent
        if (index in firstVisibleIndex..lastVisibleIndex) return 1F
        return 0F
    }
}

fun SensitiveLazyColumnState.transform(indexMapping: (Int) -> Int): SensitiveLazyColumnState{
    return this.copy(
        firstVisibleIndex = indexMapping(firstVisibleIndex),
        lastVisibleIndex = indexMapping(lastVisibleIndex),
    )
}
