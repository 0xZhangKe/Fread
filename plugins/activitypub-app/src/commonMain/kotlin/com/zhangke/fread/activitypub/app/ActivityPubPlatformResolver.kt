package com.zhangke.fread.activitypub.app

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformSnapshot
import me.tatarka.inject.annotations.Inject

class ActivityPubPlatformResolver @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
) : IPlatformResolver {

    override suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        return platformRepo.getSuggestedPlatformSnapshotList()
    }

    override suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>? {
        if (blogSnapshot.protocol.notActivityPub) return null
        val baseUrl = FormalBaseUrl.parse(blogSnapshot.domain) ?: return null
        return platformRepo.getPlatform(baseUrl)
    }
}
