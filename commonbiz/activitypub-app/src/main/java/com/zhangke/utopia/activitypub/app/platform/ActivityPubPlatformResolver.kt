package com.zhangke.utopia.activitypub.app.platform

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypub.app.internal.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.activitypub.app.internal.uri.timeline.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypub.app.internal.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.platform.UtopiaPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

@Filt
class ActivityPubPlatformResolver @Inject constructor(
    private val getActivityPubServer: GetActivityPubPlatformUseCase,
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
    private val parseTimelineUri: ParseUriToTimelineUriUseCase,
) : IPlatformResolver {

    override suspend fun resolveBySourceUri(sourceUri: StatusProviderUri): Result<UtopiaPlatform?> {
        var host = parseUriToUserUriUseCase(sourceUri)?.finger?.host
        if (host.isNullOrEmpty()) {
            host = parseTimelineUri(sourceUri)?.timelineServerHost
        }
        if (host.isNullOrEmpty()) {
            return Result.failure(IllegalArgumentException("$sourceUri not ActivityPub uri"))
        }
        return getActivityPubServer(host)
    }
}