package com.zhangke.fread.common

import com.zhangke.fread.common.daynight.ActivityDayNightHelper

expect interface CommonActivityPlatformComponent

interface CommonActivityComponent : CommonActivityPlatformComponent {
    val activityDayNightHelper: ActivityDayNightHelper
}