package com.zhangke.utopia.activitypub.app.internal.screen.addinstance

import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstance

data class AddInstanceUiState(
    val query: String?,
    val searching: Boolean,
    val errorMessage: TextString?,
    val inInstanceDetailPage: Boolean,
    val instance: ActivityPubInstance?,
)
