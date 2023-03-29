package com.zhangke.utopia.composable.source.maintainer

import com.zhangke.utopia.status.source.StatusSourceMaintainer

data class StatusSourceUiState(
    val uri: String,
    val nickName: String,
    val description: String,
    val thumbnail: String?,
    val selected: Boolean,
    val onSaveToLocal: suspend () -> Unit,
    val onRequestMaintainer: suspend () -> StatusSourceMaintainer,
)