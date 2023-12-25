package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ChooseBaseUrlUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val timelineUriTransformer: TimelineUriTransformer,
    private val userUriTransformer: UserUriTransformer,
    private val getBaseUrlFromWebFinger: GetBaseUrlFromWebFingerUseCase,
    private val getBasicCommonBaseUrl: GetBasicCommonBaseUrlUseCase,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
    ): String {
        timelineUriTransformer.parse(sourceUri)
            ?.serverBaseUrl
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it }
        accountManager.getActiveAccount()
            ?.baseUrl
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it }
        accountManager.getAllLoggedAccount()
            .firstOrNull()
            ?.baseUrl
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it }
        userUriTransformer.parse(sourceUri)
            ?.webFinger
            ?.let { getBaseUrlFromWebFinger(it) }
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it }
        return getBasicCommonBaseUrl()
    }
}
