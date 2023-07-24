package com.zhangke.utopia.activitypubapp.account.adapter

import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.utopia.activitypubapp.account.repo.ActivityPubLoggedAccountEntity
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.user.LoggedAccount
import javax.inject.Inject

class ActivityPubLoggedAccountAdapter @Inject constructor() {

    fun adapt(
        entity: ActivityPubLoggedAccountEntity,
        validate: Boolean
    ): LoggedAccount {
        return LoggedAccount(
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

    fun recovery(
        user: LoggedAccount,
        uri: String,
        webFinger: WebFinger,
        token: ActivityPubTokenEntity,
    ): ActivityPubLoggedAccountEntity {
        return ActivityPubLoggedAccountEntity(
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
