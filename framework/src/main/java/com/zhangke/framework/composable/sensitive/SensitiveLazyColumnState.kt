package com.zhangke.framework.composable.sensitive

data class SensitiveLazyColumnState(
    val firstVisibleIndex: Int,
    val firstVisiblePercent: Float,
    val lastVisibleIndex: Int,
    val lastVisiblePercent: Float,
    val scrollState: ScrollState,
    val velocity: Float,
) {

    enum class ScrollState {

        DRAGGING,
        FLINGING,
    }
}
