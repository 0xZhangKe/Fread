package com.zhangke.utopia.activitypubapp.screen.server

import com.zhangke.utopia.activitypubapp.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypubapp.model.ActivityPubUser

internal data class ServerDetailUiState(
    val domain: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val version: String,
    val activeMonth: Int,
    val languages: List<String>,
    val contract: ServerDetailContract,
    val rules: List<ActivityPubInstanceRule>,
    val tabs: List<ServerDetailTab>,
)

internal data class ServerDetailContract(
    val email: String,
    val account: ActivityPubUser,
)
