package com.zhangke.utopia.activitypub.app.internal.screen.content

import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.IdentityRole

data class ActivityPubContentUiState(
    val role: IdentityRole,
    val config: ContentConfig.ActivityPubContent,
)
