package com.zhangke.fread.activitypub.app.internal.platform

import com.zhangke.activitypub.api.AppsRepo

object FreadApplicationRegisterInfo {

    const val CLIENT_NAME = "Fread"
    val redirectUris = listOf("freadapp://fread.xyz")
    val scopes = AppsRepo.AppScopes.ALL
    const val WEBSITE = "https://fread.xyz"
}
