package com.zhangke.utopia.activitypub.app.internal.source

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class UserSourceTransformer @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
) {

    fun createByUserEntity(entity: ActivityPubAccountEntity): StatusSource {
        val webFinger = accountEntityAdapter.toWebFinger(entity)
        val uri = userUriTransformer.build(webFinger, FormalBaseUrl.parse(entity.url)!!)
        return StatusSource(
            uri = uri,
            name = entity.displayName,
            description = entity.note,
            thumbnail = entity.avatar,
        )
    }
}
