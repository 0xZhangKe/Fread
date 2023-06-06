package com.zhangke.utopia.activitypubapp.auth

import com.zhangke.utopia.activitypubapp.protocol.isActivityPubUri
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserRepo
import com.zhangke.utopia.status.auth.ISourceListAuthValidateUseCase
import com.zhangke.utopia.status.source.StatusProviderUri
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ActivityPubSourceListAuthValidateUseCase @Inject constructor(
    private val userRepo: ActivityPubUserRepo,
    private val userValidateUseCase: ActivityPubUserValidationUseCase
) : ISourceListAuthValidateUseCase {

    override suspend fun invoke(sourceList: List<StatusSource>): Result<Boolean> {
        sourceList.mapNotNull { StatusProviderUri.create(it.uri) }
            .firstOrNull { it.isActivityPubUri() }
            ?: return Result.success(true)
        val user = userRepo.getCurrentUser() ?: return Result.success(false)
        return userValidateUseCase(user)
    }
}
