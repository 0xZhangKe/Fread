package com.zhangke.framework.composable.sensitive

data class SensitiveLazyColumnState(
    val firstVisibleIndex: Int,
    val firstVisiblePercent: Float,
    val lastVisibleIndex: Int,
    val lastVisiblePercent: Float,
    val isScrollInProgress: Boolean,
)
