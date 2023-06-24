package com.zhangke.utopia.activitypubapp.auth

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.uri.ActivityPubUriValidateUseCase
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserRepo
import com.zhangke.utopia.status.auth.ISourceListAuthValidateUseCase
import com.zhangke.utopia.status.auth.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

@Filt
class ActivityPubSourceListAuthValidateUseCase @Inject constructor(
    private val userRepo: ActivityPubUserRepo,
    private val userValidateUseCase: ActivityPubUserValidationUseCase,
    private val activityPubUriValidateUseCase: ActivityPubUriValidateUseCase,
) : ISourceListAuthValidateUseCase {

    override suspend fun invoke(
        sourceList: List<StatusSource>,
    ): Result<SourcesAuthValidateResult> {
        val activityPubSourceList = sourceList.filter {
            val uri = StatusProviderUri.create(it.uri)
            if (uri == null) {
                false
            } else {
                activityPubUriValidateUseCase(uri)
            }
        }
        if (activityPubSourceList.isEmpty()) {
            return Result.success(SourcesAuthValidateResult(emptyList(), emptyList()))
        }
        val user = userRepo.getCurrentUser()
        if (user != null) {
            val userValidateResult = userValidateUseCase(user)
            if (userValidateResult.isFailure) {
                return Result.failure(userValidateResult.exceptionOrNull()!!)
            }
            if (userValidateResult.getOrThrow()) {
                return Result.success(
                    SourcesAuthValidateResult(
                        validateList = activityPubSourceList,
                        invalidateList = emptyList(),
                    )
                )
            }
        }
        return Result.success(
            SourcesAuthValidateResult(
                validateList = emptyList(),
                invalidateList = activityPubSourceList,
            )
        )
    }
}
