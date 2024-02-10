package com.zhangke.utopia.activitypub.app.internal.screen.account

data class EditAccountUiState(
    val name: String,
    val banner: String,
    val avatar: String,
    val description: String,
    val fieldList: List<EditAccountFieldUiState>,
)

data class EditAccountFieldUiState(
    val idForUi: Int,
    val name: String,
    val value: String,
)
