package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.FindHostFromUriUseCase
import com.zhangke.utopia.activitypub.app.internal.source.timeline.TimelineSource
import com.zhangke.utopia.activitypub.app.internal.source.user.UserSource
import com.zhangke.utopia.status.auth.AuthWithStatusSourceLauncher
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

@Filt
class LaunchActivityPubAuthUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val author: ActivityPubOAuthor,
    private val findHostFromUriUseCase: FindHostFromUriUseCase,
) : AuthWithStatusSourceLauncher {

    override fun applicable(source: StatusSource): Boolean {
        return source is UserSource || source is TimelineSource
    }

    override suspend fun launch(source: StatusSource): Result<Boolean> {
        val host = findHostFromUriUseCase(source.uri)
        if (host.isNullOrEmpty()) {
            return Result.failure(IllegalArgumentException("Illegal source:$source"))
        }
        val client = obtainActivityPubClientUseCase(host)
        return Result.success(author.startOauth(client.buildOAuthUrl(), client))
    }
}
