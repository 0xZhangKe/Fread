package com.zhangke.utopia.activitypubapp.resolvers

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.ActivityPubMaintainer
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.source.user.UserSourceRepo
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.source.StatusSourceMaintainer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supported url and WebFinger
 */
@Singleton
internal class UserSourceMaintainerResolver @Inject constructor(
    private val userSourceRepo: UserSourceRepo,
    private val accountAdapter: ActivityPubAccountAdapter,
) : IActivityPubSourceMaintainerResolver {

    override suspend fun resolve(query: String): Result<StatusSourceMaintainer> {
        val webFinger = WebFinger.create(query) ?: return Result.failure(
            IllegalArgumentException("Illegal argument for $query")
        )
        val client = obtainActivityPubClient(webFinger.host)
        val userSource = resolveByWebFinger(webFinger)
        if (userSource.isFailure) return Result.failure(userSource.exceptionOrNull()!!)
        return client.instanceRepo
            .getInstanceInformation()
            .map {
                ActivityPubMaintainer.fromActivityPubInstance(it, listOf(userSource.getOrThrow()))
            }
    }

    private suspend fun resolveByWebFinger(webFinger: WebFinger): Result<UserSource> {
        userSourceRepo.query(webFinger)?.let {
            return Result.failure(
                IllegalArgumentException("$webFinger not found.")
            )
        }
        val client = obtainActivityPubClient(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .map { accountAdapter.adapt(it) }
            .also { result -> result.getOrNull()?.let { userSourceRepo.save(it) } }
    }
}
