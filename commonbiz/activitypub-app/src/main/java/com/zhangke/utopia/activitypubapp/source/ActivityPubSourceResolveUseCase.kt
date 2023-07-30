package com.zhangke.utopia.activitypubapp.source

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.source.timeline.ResolveTimelineSourceByUriUseCase
import com.zhangke.utopia.activitypubapp.source.user.ResolveUserSourceByUriUseCase
import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.search.IResolveSourceByUriUseCase
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

@Filt
class ActivityPubSourceResolveUseCase @Inject constructor(
    private val resolveUserSource: ResolveUserSourceByUriUseCase,
    private val resolveTimelineSourceUseCase: ResolveTimelineSourceByUriUseCase,
    private val parseUriToUserUri: ParseUriToUserUriUseCase,
) : IResolveSourceByUriUseCase {

    override suspend fun invoke(uri: StatusProviderUri): Result<StatusSource?> {
        parseUriToUserUri(uri)?.let { resolveUserSource(it) }?.let { return it }
        resolveTimelineSourceUseCase(uri)
            .takeIf { it.getOrNull() != null }
            ?.let { return it }
        return Result.success(null)
    }
}
