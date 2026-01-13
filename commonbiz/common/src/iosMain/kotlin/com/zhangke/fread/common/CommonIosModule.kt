package com.zhangke.fread.common

import com.zhangke.fread.common.browser.IosSystemBrowserLauncher
import com.zhangke.fread.common.browser.SystemBrowserLauncher
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.language.ActivityLanguageHelper
import com.zhangke.fread.common.utils.MediaFileHelper
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.common.utils.StorageHelper
import com.zhangke.fread.common.utils.ToastHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

actual fun Module.createPlatformModule() {
    singleOf(::MediaFileHelper)
    singleOf(::PlatformUriHelper)
    singleOf(::StorageHelper)
    singleOf(::ToastHelper)
    singleOf(::ActivityLanguageHelper)
    singleOf(::TextHandler)
    singleOf(::IosSystemBrowserLauncher) bind SystemBrowserLauncher::class
}
