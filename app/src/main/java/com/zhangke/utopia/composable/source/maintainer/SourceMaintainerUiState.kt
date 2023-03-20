package com.zhangke.utopia.composable.source.maintainer

data class SourceMaintainerUiState(
    val url: String,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val sourceList: List<StatusSourceUiState>,
)
