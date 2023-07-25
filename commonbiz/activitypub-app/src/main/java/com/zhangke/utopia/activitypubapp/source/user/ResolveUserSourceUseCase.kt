package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.activitypubapp.user.UserRepo
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

class ResolveUserSourceUseCase @Inject constructor(
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
    private val userRepo: UserRepo,
    private val userSourceAdapter: UserSourceAdapter,
) {

    suspend operator fun invoke(query: String): Result<UserSource?> {
        var webFinger = StatusProviderUri.create(query)
            ?.let { parseUriToUserUriUseCase(it)?.finger }
        if (webFinger == null) {
            webFinger = WebFinger.create(query)
        }
        webFinger ?: return Result.success(null)
        return userRepo.lookup(webFinger)
            .map { user ->
                user?.let { userSourceAdapter.adapt(it) }
            }
    }
}
