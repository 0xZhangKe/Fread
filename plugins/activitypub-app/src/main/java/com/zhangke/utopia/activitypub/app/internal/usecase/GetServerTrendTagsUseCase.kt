package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.IdentityRole
import javax.inject.Inject

class GetServerTrendTagsUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val activityPubTagAdapter: ActivityPubTagAdapter,
) {

    suspend operator fun invoke(role: IdentityRole): Result<List<Hashtag>> {
        return clientManager.getClient(role)
            .instanceRepo
            .getTrendsTags(
                limit = 10,
                offset = 0,
            ).map { list ->
                list.map { activityPubTagAdapter.adapt(it) }
            }
    }
}
