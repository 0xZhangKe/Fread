package com.zhangke.fread.common.ai.image

import android.os.Build

actual class ImageDescriptionAiGeneratorChecker {

    actual fun available(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
}
