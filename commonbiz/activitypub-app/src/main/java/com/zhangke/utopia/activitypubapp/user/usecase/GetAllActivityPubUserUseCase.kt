package com.zhangke.utopia.activitypubapp.user.usecase

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.user.ActivityPubUserAdapter
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserRepo
import com.zhangke.utopia.status.user.IGetAllUserUseCase
import com.zhangke.utopia.status.user.UtopiaUser
import javax.inject.Inject

@Filt
class GetAllActivityPubUserUseCase @Inject constructor(
    private val userRepo: ActivityPubUserRepo,
    private val userAdapter: ActivityPubUserAdapter,
    private val validationUseCase: ActivityPubUserValidationUseCase,
) : IGetAllUserUseCase {

    override suspend fun invoke(): Result<List<UtopiaUser>> {
        val userList = userRepo.queryAll().map { user ->
            val validate = validationUseCase(user).isSuccess
            userAdapter.adapt(user, validate)
        }
        return Result.success(userList)
    }
}
