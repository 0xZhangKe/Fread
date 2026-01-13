package com.zhangke.fread.common

import com.zhangke.fread.common.browser.AndroidSystemBrowserLauncher
import com.zhangke.fread.common.browser.OAuthHandler
import com.zhangke.fread.common.browser.SystemBrowserLauncher
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.language.ActivityLanguageHelper
import com.zhangke.fread.common.language.LanguageHelper
import com.zhangke.fread.common.utils.MediaFileHelper
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.common.utils.StorageHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

actual fun Module.createPlatformModule() {
    singleOf(::MediaFileHelper)
    singleOf(::PlatformUriHelper)
    singleOf(::StorageHelper)
    singleOf(::ActivityLanguageHelper)
    singleOf(::LanguageHelper)
    singleOf(::TextHandler)
    singleOf(::OAuthHandler)
    singleOf(::AndroidSystemBrowserLauncher) bind SystemBrowserLauncher::class
}
