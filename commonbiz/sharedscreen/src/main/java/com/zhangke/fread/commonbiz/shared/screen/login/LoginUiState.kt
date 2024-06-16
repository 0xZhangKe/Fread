package com.zhangke.fread.commonbiz.shared.screen.login

import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot

data class LoginUiState(
    val query: String,
    val platformList: List<SearchPlatformForLogin>,
    val loading: Boolean,
)

sealed interface SearchPlatformForLogin{

    data class Snapshot(val snapshot: PlatformSnapshot): SearchPlatformForLogin

    data class Platform(val platform: BlogPlatform): SearchPlatformForLogin
}
