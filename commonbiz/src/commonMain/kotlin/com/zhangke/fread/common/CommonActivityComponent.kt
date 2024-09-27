package com.zhangke.fread.common

import com.zhangke.fread.common.daynight.ActivityDayNightHelper
import com.zhangke.fread.common.handler.ActivityTextHandler
import com.zhangke.fread.common.language.ActivityLanguageHelper
import com.zhangke.fread.common.utils.ToastHelper


expect interface CommonActivityPlatformComponent

interface CommonActivityComponent : CommonActivityPlatformComponent {
    val activityLanguageHelper: ActivityLanguageHelper
    val activityDayNightHelper: ActivityDayNightHelper
    val activityTextHandler: ActivityTextHandler
    val toastHelper: ToastHelper
}