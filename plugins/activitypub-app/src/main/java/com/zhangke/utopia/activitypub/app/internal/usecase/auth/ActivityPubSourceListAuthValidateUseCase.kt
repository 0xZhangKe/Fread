package com.zhangke.utopia.activitypub.app.internal.usecase.auth

import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ActivityPubSourceListAuthValidateUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val userUriTransformer: UserUriTransformer,
) {

    suspend operator fun invoke(
        sourceList: List<StatusSource>,
    ): Result<SourcesAuthValidateResult> {
        val activityPubSourceList = sourceList.filter {
            userUriTransformer.parse(it.uri) != null
        }
        if (activityPubSourceList.isEmpty()) {
            return Result.success(SourcesAuthValidateResult(emptyList(), emptyList()))
        }
        if (accountManager.getAllLoggedAccount().isNotEmpty()) {
            return Result.success(
                SourcesAuthValidateResult(
                    validateList = activityPubSourceList,
                    invalidateList = emptyList(),
                )
            )
        }
        return Result.success(
            SourcesAuthValidateResult(
                validateList = emptyList(),
                invalidateList = activityPubSourceList,
            )
        )
    }
}
