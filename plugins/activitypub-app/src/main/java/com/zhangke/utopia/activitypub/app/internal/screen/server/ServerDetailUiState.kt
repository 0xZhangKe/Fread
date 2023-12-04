package com.zhangke.utopia.activitypub.app.internal.screen.server

import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule
import com.zhangke.utopia.status.author.BlogAuthor

internal data class ServerDetailUiState(
    val loading: Boolean,
    val baseUrl: String,
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
    val account: BlogAuthor,
)
