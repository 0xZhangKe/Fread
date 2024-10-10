package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubInstanceInfoEntity
import com.zhangke.fread.activitypub.app.internal.uri.PlatformUriTransformer
import me.tatarka.inject.annotations.Inject

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
