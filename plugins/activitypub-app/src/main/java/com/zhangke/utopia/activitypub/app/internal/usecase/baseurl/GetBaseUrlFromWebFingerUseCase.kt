package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import javax.inject.Inject

class GetBaseUrlFromWebFingerUseCase @Inject constructor(
    private val activityPubDatabases: ActivityPubDatabases,
    private val getBasicCommonBaseUrl: GetBasicCommonBaseUrlUseCase,
) {

    suspend operator fun invoke(webFinger: WebFinger): String {
        activityPubDatabases.getWebFingerToBaseUrlDao().queryBaseUrl(webFinger)?.let {
            return it
        }

    }
}
