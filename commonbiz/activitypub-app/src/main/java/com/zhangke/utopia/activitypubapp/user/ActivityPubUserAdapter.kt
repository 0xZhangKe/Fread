package com.zhangke.utopia.activitypubapp.user

import com.zhangke.activitypub.entry.ActivityPubToken
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserEntity
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.user.UtopiaUser
import javax.inject.Inject

class ActivityPubUserAdapter @Inject constructor() {

    fun adapt(
        entity: ActivityPubUserEntity,
        validate: Boolean
    ): UtopiaUser {
        return UtopiaUser(
            id = entity.id,
            platform = entity.platform,
            host = entity.host,
            name = entity.name,
            description = entity.description,
            avatar = entity.avatar,
            homepage = entity.homepage,
            active = entity.active,
            validate = validate,
        )
    }

    fun revert(
        user: UtopiaUser,
        uri: String,
        webFinger: WebFinger,
        token: ActivityPubToken,
    ): ActivityPubUserEntity {
        return ActivityPubUserEntity(
            uri = uri,
            id = webFinger.toString(),
            platform = user.platform,
            host = user.host,
            name = user.name,
            description = user.description,
            avatar = user.avatar,
            homepage = user.homepage,
            active = user.active,
            token = token,
        )
    }
}
