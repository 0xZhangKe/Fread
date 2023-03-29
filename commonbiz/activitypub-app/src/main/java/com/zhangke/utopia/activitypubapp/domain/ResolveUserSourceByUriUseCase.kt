package com.zhangke.utopia.activitypubapp.domain

import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

internal class ResolveUserSourceByUriUseCase @Inject constructor(
    private val useCase: ResolveUserSourceByWebFingerUseCase,
) {

    suspend operator fun invoke(uri: String): UserSource? {
        val webFinger = WebFinger.create(uri) ?: return null
        return useCase(webFinger)
    }
}