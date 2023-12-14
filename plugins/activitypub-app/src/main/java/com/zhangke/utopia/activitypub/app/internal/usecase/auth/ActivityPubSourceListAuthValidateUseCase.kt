package com.zhangke.utopia.activitypub.app.internal.usecase.auth

import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ActivityPubUriValidateUseCase
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ActivityPubSourceListAuthValidateUseCase @Inject constructor(
    private val userRepo: ActivityPubLoggedAccountRepo,
    private val userValidateUseCase: ActivityPubAccountValidationUseCase,
    private val activityPubUriValidate: ActivityPubUriValidateUseCase,
) {

    suspend operator fun invoke(
        sourceList: List<StatusSource>,
    ): Result<SourcesAuthValidateResult> {
        val activityPubSourceList = sourceList.filter {
            activityPubUriValidate(it.uri)
        }
        if (activityPubSourceList.isEmpty()) {
            return Result.success(SourcesAuthValidateResult(emptyList(), emptyList()))
        }
        val user = userRepo.getCurrentAccount()
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
