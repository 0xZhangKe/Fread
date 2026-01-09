package com.zhangke.fread.di

import com.zhangke.fread.common.CommonActivityComponent
import com.zhangke.fread.utils.ActivityHelper

interface HostingActivityComponent : CommonActivityComponent {
    val activityHelper: ActivityHelper
}
