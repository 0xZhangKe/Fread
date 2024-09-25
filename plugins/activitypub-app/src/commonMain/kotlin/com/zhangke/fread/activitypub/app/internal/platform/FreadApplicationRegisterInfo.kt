package com.zhangke.fread.activitypub.app.internal.platform

import com.zhangke.activitypub.api.AppsRepo
import com.zhangke.fread.common.config.AppCommonConfig

object FreadApplicationRegisterInfo {

    const val CLIENT_NAME = "Fread"
    val redirectUris = listOf("freadapp://fread.xyz")
    val scopes = AppsRepo.AppScopes.ALL
    const val WEBSITE = AppCommonConfig.WEBSITE
}
