package com.zhangke.utopia.activitypub.app.internal.platform

import com.zhangke.activitypub.api.AppsRepo

object UtopiaApplicationRegisterInfo {

    const val clientName = "Utopia"
    val redirectUris = listOf("utopia://oauth.utopia")
    val scopes = AppsRepo.AppScopes.ALL
    const val website = "https://0xzhangke.github.io/"
}
