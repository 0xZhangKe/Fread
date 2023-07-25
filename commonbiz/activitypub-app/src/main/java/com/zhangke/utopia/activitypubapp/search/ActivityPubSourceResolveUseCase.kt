package com.zhangke.utopia.activitypubapp.search

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.usecase.ResolveTimelineSourceUseCase
import com.zhangke.utopia.activitypubapp.source.user.ResolveUserSourceUseCase
import com.zhangke.utopia.status.search.IResolveSourceByUriUseCase
import com.zhangke.utopia.status.utils.StatusProviderUri
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

@Filt
class ActivityPubSourceResolveUseCase @Inject constructor(
    private val resolveUserSourceUseCase: ResolveUserSourceUseCase,
    private val resolveTimelineSourceUseCase: ResolveTimelineSourceUseCase,
) : IResolveSourceByUriUseCase {

    override suspend fun invoke(uri: StatusProviderUri): Result<StatusSource?> {
        resolveUserSourceUseCase(uri.toString())
            .takeIf { it.getOrNull() != null }
            ?.let { return it }
        resolveTimelineSourceUseCase(uri)
            .takeIf { it.getOrNull() != null }
            ?.let { return it }
        return Result.success(null)
    }
}
