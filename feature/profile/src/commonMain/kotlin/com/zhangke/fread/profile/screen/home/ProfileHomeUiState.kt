package com.zhangke.fread.profile.screen.home

import com.zhangke.fread.status.model.LoggedAccountDetail

data class ProfileHomeUiState(
    val pageLoading: Boolean = true,
    val accountDataList: List<ProfileAccountUiState> = emptyList(),
)

data class ProfileAccountUiState(
    val account: LoggedAccountDetail,
    val authFailed: Boolean,
    val active: Boolean,
)
