package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubInstanceInfoEntity
import com.zhangke.utopia.activitypub.app.internal.uri.PlatformUriTransformer
import javax.inject.Inject

class ActivityPubPlatformEntityAdapter @Inject constructor(
    private val uriTransformer: PlatformUriTransformer,
) {

    fun toEntity(
        baseUrl: FormalBaseUrl,
        entity: ActivityPubInstanceEntity
    ): ActivityPubInstanceInfoEntity {
        return ActivityPubInstanceInfoEntity(
            uri = uriTransformer.build(baseUrl).toString(),
            baseUrl = baseUrl,
            instanceEntity = entity,
        )
    }
}
