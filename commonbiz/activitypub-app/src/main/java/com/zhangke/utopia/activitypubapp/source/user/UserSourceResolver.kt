package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.uri.user.ActivityPubUserUriValidateUseCase
import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.resolvers.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

@Filt
class UserSourceResolver @Inject constructor(
    private val repo: UserSourceRepo,
    private val userUriValidateUseCase: ActivityPubUserUriValidateUseCase,
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
) : IStatusSourceResolver {

    override fun applicable(uri: StatusProviderUri): Boolean {
        return userUriValidateUseCase(uri)
    }

    override suspend fun resolve(uri: StatusProviderUri): Result<StatusSource> {
        val userUri = parseUriToUserUriUseCase(uri) ?: return Result.failure(
            IllegalArgumentException("$uri is not a UserSource!")
        )
        return repo.query(userUri.finger)?.let { Result.success(it) } ?: Result.failure(
            IllegalArgumentException("$uri not found!")
        )
    }
}
