package com.zhangke.utopia.activitypubapp.domain

import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

class ActivityPubAccountToWebFingerUseCase @Inject constructor() {

    operator fun invoke(account: ActivityPubAccount): WebFinger {
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
