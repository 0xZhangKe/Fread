package com.zhangke.utopia.activitypubapp.auth

import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.domain.FindHostFromUriUseCase
import com.zhangke.utopia.status.auth.IPerformAuthBySourceListUseCase
import javax.inject.Inject

@Filt
class PerformActivityPubAuthUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val author: ActivityPubOAuthor,
    private val findHostFromUriUseCase: FindHostFromUriUseCase,
) : IPerformAuthBySourceListUseCase {

    override suspend fun invoke(sourceUriList: List<String>): Result<Boolean> {
        val host = sourceUriList.mapFirstOrNull {
            findHostFromUriUseCase(it)
        }
        if (host.isNullOrEmpty()) return Result.success(true)
        val client = obtainActivityPubClientUseCase(host)
        return Result.success(author.startOauth(client.buildOAuthUrl(), client))
    }
}
