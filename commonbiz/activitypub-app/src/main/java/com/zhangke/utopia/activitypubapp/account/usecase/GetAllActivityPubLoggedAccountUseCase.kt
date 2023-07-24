package com.zhangke.utopia.activitypubapp.account.usecase

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.account.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.auth.ActivityPubAccountValidationUseCase
import com.zhangke.utopia.activitypubapp.account.repo.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.status.user.IGetAllUserUseCase
import com.zhangke.utopia.status.user.LoggedAccount
import javax.inject.Inject

@Filt
class GetAllActivityPubLoggedAccountUseCase @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val accountAdapter: ActivityPubLoggedAccountAdapter,
    private val validationAccountCase: ActivityPubAccountValidationUseCase,
) : IGetAllUserUseCase {

    override suspend fun invoke(): Result<List<LoggedAccount>> {
        val userList = accountRepo.queryAll().map { user ->
            val validate = validationAccountCase(user).isSuccess
            accountAdapter.adapt(user, validate)
        }
        return Result.success(userList)
    }
}
