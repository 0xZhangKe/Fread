package com.zhangke.utopia.activitypub.app.internal.source

import com.zhangke.utopia.activitypub.app.internal.source.timeline.ResolveTimelineSourceByUriUseCase
import com.zhangke.utopia.activitypub.app.internal.source.user.ResolveUserSourceByUriUseCase
import com.zhangke.utopia.activitypub.app.internal.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubSourceResolveUseCase @Inject constructor(
    private val resolveUserSource: ResolveUserSourceByUriUseCase,
    private val resolveTimelineSourceUseCase: ResolveTimelineSourceByUriUseCase,
    private val parseUriToUserUri: ParseUriToUserUriUseCase,
) {

    suspend operator fun invoke(uri: StatusProviderUri): Result<StatusSource?> {
        parseUriToUserUri(uri)?.let { resolveUserSource(it) }?.let { return it }
        resolveTimelineSourceUseCase(uri)
            .takeIf { it.getOrNull() != null }
            ?.let { return it }
        return Result.success(null)
    }
}
