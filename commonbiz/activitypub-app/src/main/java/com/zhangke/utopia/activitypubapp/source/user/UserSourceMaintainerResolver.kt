package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.ActivityPubMaintainer
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status_provider.StatusSourceMaintainer
import com.zhangke.utopia.status_provider.IStatusSourceMaintainerResolver

/**
 * Supported url and WebFinger
 */
internal class UserSourceMaintainerResolver : IStatusSourceMaintainerResolver {

    override suspend fun resolve(content: String): StatusSourceMaintainer? {
        val webFinger = WebFinger.create(content) ?: return null
        val client = obtainActivityPubClient(webFinger.host)
        val userSource = client.accountRepo
            .lookup(webFinger.toString())
            .getOrThrow()
            .toUserSource(webFinger)
        return client.instanceRepo
            .getInstanceInformation()
            .getOrNull()
            ?.let {
                ActivityPubMaintainer.fromActivityPubInstance(it, listOf(userSource))
            }
    }

    private fun ActivityPubAccount.toUserSource(webFinger: WebFinger): UserSource {
        return UserSource(
            nickName = displayName,
            description = note,
            thumbnail = avatar,
            webFinger = webFinger,
        )
    }
}