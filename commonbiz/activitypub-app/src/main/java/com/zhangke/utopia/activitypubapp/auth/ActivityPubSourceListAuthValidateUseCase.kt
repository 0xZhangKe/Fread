package com.zhangke.utopia.activitypubapp.auth

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.protocol.isActivityPubUri
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserRepo
import com.zhangke.utopia.status.auth.ISourceListAuthValidateUseCase
import com.zhangke.utopia.status.source.StatusProviderUri
import javax.inject.Inject

@Filt
class ActivityPubSourceListAuthValidateUseCase @Inject constructor(
    private val userRepo: ActivityPubUserRepo,
    private val userValidateUseCase: ActivityPubUserValidationUseCase
) : ISourceListAuthValidateUseCase {

    override suspend fun invoke(sourceUriList: List<String>): Result<Boolean> {
        sourceUriList.mapNotNull { StatusProviderUri.create(it) }
            .firstOrNull { it.isActivityPubUri() }
            ?: return Result.success(true)
        val user = userRepo.getCurrentUser() ?: return Result.success(false)
        return userValidateUseCase(user)
    }
}
