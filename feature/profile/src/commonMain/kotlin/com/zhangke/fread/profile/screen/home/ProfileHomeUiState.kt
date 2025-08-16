package com.zhangke.fread.profile.screen.home

import com.zhangke.fread.status.model.LoggedAccountDetail

data class ProfileHomeUiState(
    val accountDataList: List<ProfileAccountUiState>,
)

data class ProfileAccountUiState(
    val account: LoggedAccountDetail,
    val authFailed: Boolean,
    val active: Boolean,
)
