package com.zhangke.utopia.activitypub.app.internal.usecase.source.timeline

import com.zhangke.utopia.activitypub.app.internal.source.TimelineSourceTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ResolveTimelineSourceByUriUseCase @Inject constructor(
    private val timelineUriTransformer: TimelineUriTransformer,
    private val timelineSourceTransformer: TimelineSourceTransformer,
    private val getPlatform: GetActivityPubPlatformUseCase,
) {

    suspend operator fun invoke(uri: FormalUri): Result<StatusSource?> {
        val timelineUriData = timelineUriTransformer.parse(uri) ?: return Result.success(null)
        val platform = getPlatform(timelineUriData.serverBaseUrl).getOrNull() ?: return Result.success(null)
        return Result.success(
            timelineSourceTransformer.createByPlatform(uri, platform)
        )
    }
}
