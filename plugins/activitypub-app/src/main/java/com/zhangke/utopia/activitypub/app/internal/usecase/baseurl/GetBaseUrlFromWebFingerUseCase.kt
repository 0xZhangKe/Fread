package com.zhangke.utopia.activitypub.app.internal.usecase.baseurl

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerToBaseUrlRepo
import javax.inject.Inject

class GetBaseUrlFromWebFingerUseCase @Inject constructor(
    private val activityPubRepo: WebFingerToBaseUrlRepo,
    private val clientManager: ActivityPubClientManager,
    private val getBasicCommonBaseUrl: GetBasicCommonBaseUrlUseCase,
) {

    suspend operator fun invoke(webFinger: WebFinger): Result<String?> {
        activityPubRepo.queryBaseUrl(webFinger)
            ?.let { return Result.success(it) }
        return clientManager.getClient(getBasicCommonBaseUrl())
            .accountRepo
            .lookup(webFinger.toString())
            .map { it?.url }
            .onSuccess {
                if (it.isNullOrEmpty().not()) {
                    activityPubRepo.insert(webFinger, it!!)
                }
            }
    }
}
