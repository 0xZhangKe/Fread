package com.zhangke.utopia.activitypub.app.internal.usecase.source.user

import com.zhangke.utopia.activitypub.app.internal.model.UserSource
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import com.zhangke.utopia.activitypub.app.internal.repo.user.UserRepo
import javax.inject.Inject

class ResolveUserSourceByUriUseCase @Inject constructor(
    private val userRepo: UserRepo,
) {

    suspend operator fun invoke(uri: ActivityPubUserUri): Result<UserSource?> {
        return userRepo.getUserSource(uri)
    }
}
