package com.zhangke.utopia.activitypub.app.internal.usecase.source.user

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.model.UserSource
import com.zhangke.utopia.activitypub.app.internal.repo.user.UserRepo
import javax.inject.Inject

class ResolveUserSourceByWebFingerUseCase @Inject constructor(
    private val userRepo: UserRepo,
) {

    suspend operator fun invoke(webFingerString: String): Result<UserSource?> {
        val webFinger = WebFinger.create(webFingerString) ?: return Result.success(null)
        return userRepo.lookupUserSource(webFinger)
    }
}
