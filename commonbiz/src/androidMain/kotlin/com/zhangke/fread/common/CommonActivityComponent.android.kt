package com.zhangke.fread.common

import com.zhangke.fread.common.daynight.ActivityDayNightHelper
import com.zhangke.fread.common.language.ActivityLanguageHelper

actual interface CommonActivityPlatformComponent {
    val activityLanguageHelper: ActivityLanguageHelper
    val activityDayNightHelper: ActivityDayNightHelper
}