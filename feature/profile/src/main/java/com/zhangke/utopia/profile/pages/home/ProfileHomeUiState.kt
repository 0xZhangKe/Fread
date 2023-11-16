package com.zhangke.utopia.profile.pages.home

import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.BlogPlatform

data class ProfileHomeUiState(
    val accountDataList: List<Pair<BlogPlatform, List<LoggedAccount>>>,
)

