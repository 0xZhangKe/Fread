package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerToBaseUrlRepo
import javax.inject.Inject

class GetBaseUrlFromWebFingerUseCase @Inject constructor(
    private val activityPubRepo: WebFingerToBaseUrlRepo,
    private val clientManager: ActivityPubClientManager,
    private val getBasicCommonBaseUrl: GetBasicCommonBaseUrlUseCase,
) {

    suspend operator fun invoke(webFinger: WebFinger): Result<FormalBaseUrl?> {
        activityPubRepo.queryBaseUrl(webFinger)
            ?.let { return Result.success(it) }
        return clientManager.getClient(getBasicCommonBaseUrl())
            .accountRepo
            .lookup(webFinger.toString())
            .map { entity -> entity?.url?.let { FormalBaseUrl.parse(it) } }
            .onSuccess {
                if (it != null) {
                    activityPubRepo.insert(webFinger, it)
                }
            }
    }
}
