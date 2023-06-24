package com.zhangke.utopia.pages.feeds.shared.source

data class StatusSourceUiState(
    val uri: String,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val addEnabled: Boolean,
    val removeEnabled: Boolean,
)
