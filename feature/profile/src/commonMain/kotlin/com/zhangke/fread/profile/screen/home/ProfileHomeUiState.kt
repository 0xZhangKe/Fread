package com.zhangke.fread.profile.screen.home

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.platform.BlogPlatform

data class ProfileHomeUiState(
    val accountDataList: List<Pair<BlogPlatform, List<ProfileAccountUiState>>>,
)

data class ProfileAccountUiState(
    val account: LoggedAccount,
    val logged: Boolean,
)
