package com.zhangke.utopia.activitypub.app.internal.usecase.source.user

import com.zhangke.utopia.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ResolveUserSourceByUriUseCase @Inject constructor(
    private val userRepo: UserRepo,
    private val userUriTransformer: UserUriTransformer,
) {

    suspend operator fun invoke(uri: StatusProviderUri): Result<StatusSource?> {
        val uriData = userUriTransformer.parse(uri) ?: return Result.success(null)
        return userRepo.getUserSource(uriData)
    }
}
