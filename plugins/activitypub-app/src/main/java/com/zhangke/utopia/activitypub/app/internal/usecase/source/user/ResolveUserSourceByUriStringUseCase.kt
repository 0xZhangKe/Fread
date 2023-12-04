package com.zhangke.utopia.activitypub.app.internal.usecase.source.user

import com.zhangke.utopia.activitypub.app.internal.model.UserSource
import com.zhangke.utopia.activitypub.app.internal.usecase.source.user.ResolveUserSourceByUriUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseStringToUserUriUseCase
import javax.inject.Inject

class ResolveUserSourceByUriStringUseCase @Inject constructor(
    private val parseStringToUserUri: ParseStringToUserUriUseCase,
    private val resolveUserSource: ResolveUserSourceByUriUseCase,
) {

    suspend operator fun invoke(uriString: String): Result<UserSource?> {
        val uri = parseStringToUserUri(uriString) ?: return Result.success(null)
        return resolveUserSource(uri)
    }
}
