package com.zhangke.utopia.activitypub.app.internal.account.usecase

import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
) {

    suspend operator fun invoke(uri: ActivityPubUserUri) {
        accountRepo.deleteByUri(uri)
        if (accountRepo.getCurrentAccount()?.uri == uri) {
            accountRepo.queryAll()
                .firstOrNull()
                ?.let { accountRepo.updateCurrentAccount(it) }
        }
    }
}
