package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.uri.user.ActivityPubUserUri
import com.zhangke.utopia.activitypubapp.user.UserRepo
import javax.inject.Inject

class ResolveUserSourceByUriUseCase @Inject constructor(
    private val userRepo: UserRepo,
    private val userSourceAdapter: UserSourceAdapter,
) {

    suspend operator fun invoke(uri: ActivityPubUserUri): Result<UserSource?> {
        return userRepo.getUserSource(uri).map { user ->
            userSourceAdapter.adapt(user)
        }
    }
}
