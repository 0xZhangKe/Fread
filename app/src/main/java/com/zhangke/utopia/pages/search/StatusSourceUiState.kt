package com.zhangke.utopia.pages.search

data class StatusSourceUiState(
    val uri: String,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val selected: Boolean,
)
