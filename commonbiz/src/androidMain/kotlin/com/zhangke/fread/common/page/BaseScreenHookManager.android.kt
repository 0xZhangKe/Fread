package com.zhangke.fread.common.page

import com.zhangke.fread.status.utils.findImplementers

internal actual fun findBaseScreenHookImplementers(): List<BaseScreenHook> {
    return findImplementers()
}