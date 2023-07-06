package com.zhangke.utopia.activitypubapp.screen.service

import com.zhangke.utopia.activitypubapp.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypubapp.model.ActivityPubUser

data class ServerDetailUiState(
    val domain: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val version: String,
    val activeMonth: Int,
    val languages: List<String>,
    val contract: ServerDetailContract,
    val rules: List<ActivityPubInstanceRule>,
)

data class ServerDetailContract(
    val email: String,
    val account: ActivityPubUser,
)
