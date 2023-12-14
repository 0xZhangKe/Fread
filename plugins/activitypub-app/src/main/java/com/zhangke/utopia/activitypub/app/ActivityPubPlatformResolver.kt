package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubPlatformResolver @Inject constructor(
    private val getActivityPubServer: GetActivityPubPlatformUseCase,
    private val userUriTransformer: UserUriTransformer,
    private val timelineUriTransformer: TimelineUriTransformer,
) : IPlatformResolver {

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        var baseUrl = userUriTransformer.parse(sourceUri)?.webFinger?.host?.toBaseUrl()
        if (baseUrl.isNullOrEmpty()) {
            baseUrl = timelineUriTransformer.parse(sourceUri)?.serverBaseUrl
        }
        if (baseUrl.isNullOrEmpty()) {
            return Result.failure(IllegalArgumentException("$sourceUri not ActivityPub uri"))
        }
        return getActivityPubServer(baseUrl)
    }
}