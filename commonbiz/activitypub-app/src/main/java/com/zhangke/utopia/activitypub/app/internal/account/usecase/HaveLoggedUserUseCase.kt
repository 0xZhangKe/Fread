package com.zhangke.utopia.activitypub.app.internal.account.usecase

import javax.inject.Inject

class HaveLoggedUserUseCase @Inject constructor(
    private val getAllLoggedUser: GetAllActivityPubLoggedAccountUseCase,
) {

    suspend operator fun invoke(): Boolean {
        return getAllLoggedUser().getOrNull().isNullOrEmpty().not()
    }
}
