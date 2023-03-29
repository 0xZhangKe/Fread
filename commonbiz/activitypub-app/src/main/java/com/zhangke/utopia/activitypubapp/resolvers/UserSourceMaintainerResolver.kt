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

    override suspend fun resolve(query: String): StatusSourceMaintainer? {
        val webFinger = WebFinger.create(query) ?: return null
        val client = obtainActivityPubClient(webFinger.host)
        val userSource = resolveByWebFinger(webFinger)
        return client.instanceRepo
            .getInstanceInformation()
            .getOrNull()
            ?.let {
                ActivityPubMaintainer.fromActivityPubInstance(it, listOf(userSource))
            }
    }

    private suspend fun resolveByWebFinger(webFinger: WebFinger): UserSource {
        userSourceRepo.query(webFinger)?.let { return it }
        val client = obtainActivityPubClient(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .getOrThrow()
            .let { accountAdapter.adapt(it) }
            .also { userSourceRepo.save(it) }
    }
}