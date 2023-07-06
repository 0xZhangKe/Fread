package com.zhangke.utopia.activitypubapp.usecase

import com.zhangke.activitypub.entry.ActivityPubAccountEntity
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

class ActivityPubAccountToWebFingerUseCase @Inject constructor() {

    operator fun invoke(account: ActivityPubAccountEntity): WebFinger {
        WebFinger.create(account.acct)?.let { return it }
        WebFinger.create(account.url)!!.let { return it }
    }
}
