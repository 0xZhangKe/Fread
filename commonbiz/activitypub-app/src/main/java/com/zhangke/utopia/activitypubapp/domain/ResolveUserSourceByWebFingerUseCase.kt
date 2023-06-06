package com.zhangke.utopia.activitypubapp.domain

import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.source.user.UserSource
import com.zhangke.utopia.activitypubapp.source.user.UserSourceRepo
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

internal class ResolveUserSourceByWebFingerUseCase @Inject constructor(
    private val repo: UserSourceRepo,
    private val accountAdapter: ActivityPubAccountAdapter,
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) {

    suspend operator fun invoke(webFinger: WebFinger): UserSource {
        repo.query(webFinger)?.let { return it }
        val client = obtainActivityPubClientUseCase(webFinger.host)
        return client.accountRepo
            .lookup(webFinger.toString())
            .getOrThrow()
            .let { accountAdapter.createSource(it) }
            .also { repo.save(it) }
    }
}