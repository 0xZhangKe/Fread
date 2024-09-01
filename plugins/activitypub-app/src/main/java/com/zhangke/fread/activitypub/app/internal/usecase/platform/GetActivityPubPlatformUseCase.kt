package com.zhangke.fread.activitypub.app.internal.usecase.platform

import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class GetActivityPubPlatformUseCase @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
) {

    suspend operator fun invoke(role: IdentityRole): Result<BlogPlatform> {
        return platformRepo.getPlatform(role)
    }
}
