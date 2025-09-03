package com.zhangke.fread.commonbiz.shared.composable

import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.zhangke.fread.commonbiz.shared.screen.status.account.SelectAccountOpenStatusScreen
import com.zhangke.fread.status.model.StatusUiState

fun onOpenBlogWithOtherAccountClick(navigator: BottomSheetNavigator, statusUiState: StatusUiState) {
    navigator.show(SelectAccountOpenStatusScreen.create(statusUiState))
}
