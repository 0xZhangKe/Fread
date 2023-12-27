package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ChooseBaseUrlUseCase @Inject constructor(
    private val chooseBaseUrlFromLoggedAccount: ChooseBaseUrlFromLoggedAccountUseCase,
    private val timelineUriTransformer: TimelineUriTransformer,
    private val userUriTransformer: UserUriTransformer,
    private val getBaseUrlFromWebFinger: GetBaseUrlFromWebFingerUseCase,
    private val getBasicCommonBaseUrl: GetBasicCommonBaseUrlUseCase,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri? = null,
    ): FormalBaseUrl {
        if (sourceUri != null) {
            timelineUriTransformer.parse(sourceUri)
                ?.serverBaseUrl
                ?.let { return it }
        }
        chooseBaseUrlFromLoggedAccount()?.let { return it }
        if (sourceUri != null) {
            userUriTransformer.parse(sourceUri)
                ?.webFinger
                ?.let { getBaseUrlFromWebFinger(it) }
                ?.getOrNull()
                ?.let { return it }
        }
        return getBasicCommonBaseUrl()
    }
}
