package com.zhangke.utopia.activitypub.app.internal.source.user

import com.zhangke.utopia.activitypub.app.internal.uri.user.ParseStringToUserUriUseCase
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
