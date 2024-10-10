package com.zhangke.fread.activitypub.app.internal.utils

import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class MastodonHelper @Inject constructor() {
    actual fun getLocalMastodonJson(): String? {
        TODO("Not yet implemented")
    }
}