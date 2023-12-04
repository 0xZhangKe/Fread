package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.usecase.source.ActivityPubSourceResolveUseCase
import com.zhangke.utopia.status.source.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubSourceResolver @Inject constructor(
    private val resolveActivityPubSourceByUri: ActivityPubSourceResolveUseCase,
): IStatusSourceResolver {

    override suspend fun resolveSourceByUri(uri: StatusProviderUri): Result<StatusSource?> {
        return resolveActivityPubSourceByUri(uri)
    }
}
