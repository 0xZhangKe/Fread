package com.zhangke.utopia.activitypub.app.internal.usecase.status

import android.util.Log
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.usecase.client.GetClientUseCase
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import javax.inject.Inject

class IsUserFirstStatusUseCase @Inject constructor(
    private val getClientUseCase: GetClientUseCase,
) {

    suspend operator fun invoke(
        userUriInsights: UserUriInsights,
        statusId: String,
    ): Result<Boolean> {
        val userBaseUrl = userUriInsights.webFinger.host.toBaseUrl()
        return getClientUseCase(userBaseUrl).accountRepo
            .getStatuses(
                id = userUriInsights.userId,
                limit = 1,
                maxId = statusId,
            ).map { it.isEmpty() }.also {
                Log.d("U_TEST", "isUserFirstStatus($userUriInsights , $statusId) result is ${it.getOrNull()}")
            }
    }
}
