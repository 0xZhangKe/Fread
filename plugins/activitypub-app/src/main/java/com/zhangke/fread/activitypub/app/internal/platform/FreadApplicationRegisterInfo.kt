package com.zhangke.fread.activitypub.app.internal.platform

import com.zhangke.activitypub.api.AppsRepo

object FreadApplicationRegisterInfo {

    const val CLIENT_NAME = "Fread"
    val redirectUris = listOf("fread://oauth.fread")
    val scopes = AppsRepo.AppScopes.ALL
    const val website = "https://0xzhangke.github.io/"
}
