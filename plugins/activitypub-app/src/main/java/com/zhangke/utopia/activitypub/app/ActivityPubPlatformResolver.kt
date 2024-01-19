package com.zhangke.utopia.activitypub.app

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.baseurl.GetBaseUrlFromWebFingerUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubPlatformResolver @Inject constructor(
    private val getActivityPubServer: GetActivityPubPlatformUseCase,
    private val userUriTransformer: UserUriTransformer,
    private val getBaseUrlFromWebFinger: GetBaseUrlFromWebFingerUseCase,
) : IPlatformResolver {

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        var baseUrl: FormalBaseUrl? = null
        userUriTransformer.parse(sourceUri)?.also {
            val result = getBaseUrlFromWebFinger(it.webFinger)
            if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
            baseUrl = result.getOrNull()
        }
        if (baseUrl == null) {
            return Result.failure(IllegalArgumentException("$sourceUri not ActivityPub uri"))
        }
        return getActivityPubServer(baseUrl!!)
    }
}
