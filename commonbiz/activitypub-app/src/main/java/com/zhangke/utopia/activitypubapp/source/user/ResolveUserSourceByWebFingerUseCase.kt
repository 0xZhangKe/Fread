package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.user.UserRepo
import com.zhangke.framework.utils.WebFinger
import javax.inject.Inject

class ResolveUserSourceByWebFingerUseCase @Inject constructor(
    private val userRepo: UserRepo,
    private val userSourceAdapter: UserSourceAdapter,
) {

    suspend operator fun invoke(webFingerString: String): Result<UserSource?> {
        val webFinger = WebFinger.create(webFingerString) ?: return Result.success(null)
        return userRepo.lookup(webFinger).map { user ->
            user?.let(userSourceAdapter::adapt)
        }
    }
}
