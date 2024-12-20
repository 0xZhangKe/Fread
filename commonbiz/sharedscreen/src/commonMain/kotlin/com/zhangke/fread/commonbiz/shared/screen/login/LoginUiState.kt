package com.zhangke.fread.commonbiz.shared.screen.login

import com.zhangke.fread.status.platform.PlatformSnapshot

data class LoginUiState(
    val query: String,
    val platformList: List<PlatformSnapshot>,
)
