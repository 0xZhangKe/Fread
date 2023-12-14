package com.zhangke.utopia.activitypub.app.internal.usecase.source

import com.zhangke.utopia.activitypub.app.internal.usecase.source.timeline.ResolveTimelineSourceByUriUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.source.user.ResolveUserSourceByUriUseCase
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubSourceResolveUseCase @Inject constructor(
    private val resolveUserSource: ResolveUserSourceByUriUseCase,
    private val resolveTimelineSourceUseCase: ResolveTimelineSourceByUriUseCase,
) {

    suspend operator fun invoke(uri: FormalUri): Result<StatusSource?> {
        resolveUserSource(uri).takeIf { it.getOrNull() != null }?.let { return it }
        resolveTimelineSourceUseCase(uri).takeIf { it.getOrNull() != null }?.let { return it }
        return Result.success(null)
    }
}
