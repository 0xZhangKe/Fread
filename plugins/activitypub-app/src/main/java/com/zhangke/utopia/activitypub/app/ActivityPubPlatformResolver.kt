package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubPlatformResolver @Inject constructor(
    private val getActivityPubServer: GetActivityPubPlatformUseCase,
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
    private val parseTimelineUri: ParseUriToTimelineUriUseCase,
) : IPlatformResolver {

    override suspend fun resolveBySourceUri(sourceUri: StatusProviderUri): Result<BlogPlatform?> {
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