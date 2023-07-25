package com.zhangke.utopia.activitypubapp.usecase

import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.source.user.UserSourceAdapter
import com.zhangke.utopia.activitypubapp.source.user.UserSourceRepo
import com.zhangke.utopia.activitypubapp.user.ActivityPubUserAdapter
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

internal class ResolveUserSourceByWebFingerUseCase @Inject constructor(
    private val repo: UserSourceRepo,
    private val userAdapter: ActivityPubUserAdapter,
    private val sourceAdapter: UserSourceAdapter,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) {

    suspend operator fun invoke(webFinger: WebFinger): UserSource? {
        repo.query(webFinger)?.let { return it }
        val client = obtainActivityPubClientUseCase(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .getOrThrow()
            ?.let { userAdapter.adapt(it) }
            ?.let { sourceAdapter.adapt(it) }
            ?.also { repo.save(it) }
    }
}
