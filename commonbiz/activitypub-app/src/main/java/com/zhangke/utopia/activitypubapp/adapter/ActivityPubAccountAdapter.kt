package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

internal class ActivityPubAccountAdapter @Inject constructor() {

    fun adapt(account: ActivityPubAccount): UserSource {
        return UserSource(
            userId = account.id,
            nickName = account.displayName,
            description = account.note,
            thumbnail = account.avatar,
            webFinger = WebFinger.create(account.acct)!!,
        )
    }
}