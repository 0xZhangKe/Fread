package com.zhangke.utopia.activitypub.app.internal.usecase.platform

import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class GetActivityPubPlatformUseCase @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
) {

    suspend operator fun invoke(role: IdentityRole): Result<BlogPlatform> {
        return platformRepo.getPlatform(role)
    }
}
