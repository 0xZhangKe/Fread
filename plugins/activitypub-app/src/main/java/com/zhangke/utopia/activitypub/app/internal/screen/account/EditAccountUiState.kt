package com.zhangke.utopia.activitypub.app.internal.screen.account

data class EditAccountUiState(
    val name: String,
    val header: String,
    val avatar: String,
    val description: String,
    val fieldList: List<EditAccountFieldUiState>,
    val fieldAddable: Boolean,
    val requesting: Boolean,
)

data class EditAccountFieldUiState(
    val idForUi: Int,
    val name: String,
    val value: String,
)
