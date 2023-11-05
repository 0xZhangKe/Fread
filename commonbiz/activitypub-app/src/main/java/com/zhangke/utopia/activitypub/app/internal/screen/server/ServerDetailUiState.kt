package com.zhangke.utopia.activitypub.app.internal.screen.server

import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubUser

internal data class ServerDetailUiState(
    val loading: Boolean,
    val domain: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val version: String,
    val activeMonth: Int,
    val languages: List<String>,
    val contract: ServerDetailContract? = null,
    val rules: List<ActivityPubInstanceRule>,
    val tabs: List<ServerDetailTab>,
)

internal data class ServerDetailContract(
    val email: String,
    val account: ActivityPubUser,
)
