package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.common.usecase.GetDefaultBaseUrlUseCase
import com.zhangke.utopia.status.model.IdentityRole
import javax.inject.Inject

class ResolveBaseUrlUseCase @Inject constructor(
    private val loggedAccountProvider: LoggedAccountProvider,
    private val getDefaultBaseUrl: GetDefaultBaseUrlUseCase,
    private val userUriTransformer: UserUriTransformer,
) {

    operator fun invoke(role: IdentityRole): FormalBaseUrl {
        var baseUrl: FormalBaseUrl? = null
        if (role.accountUri != null) {
            baseUrl = loggedAccountProvider.getAccount(role.accountUri!!)?.platform?.baseUrl
            if (baseUrl == null) {
                baseUrl = userUriTransformer.parse(role.accountUri!!)?.baseUrl
            }
        }
        if (baseUrl == null && role.baseUrl != null) {
            baseUrl = role.baseUrl
        }
        if (baseUrl == null) {
            baseUrl = getDefaultBaseUrl()
        }
        return baseUrl
    }
}
