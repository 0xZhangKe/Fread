package com.zhangke.fread.screen.main.drawer

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.FreadContent

data class MainDrawerUiState(
    val contentConfigList: List<MainDrawerContent>
)

data class MainDrawerContent(
    val content: FreadContent,
    val account: LoggedAccount?,
)
