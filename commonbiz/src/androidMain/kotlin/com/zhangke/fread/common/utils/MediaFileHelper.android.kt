package com.zhangke.fread.common.utils

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.media.MediaFileUtil
import com.zhangke.framework.toast.showFileSaveFailedToast
import com.zhangke.framework.toast.showFileSaveSuccessToast
import com.zhangke.framework.toast.showFileSavingToast
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class MediaFileHelper @Inject constructor(
    private val context: ApplicationContext,
) {
    actual fun saveImageToGallery(url: String) {
        ApplicationScope.launch {
            showFileSavingToast()
            if (MediaFileUtil.saveImageToGallery(context, url)) {
                showFileSaveSuccessToast()
            } else {
                showFileSaveFailedToast()
            }
        }
    }

    actual fun saveVideoToGallery(url: String) {
        ApplicationScope.launch {
            showFileSavingToast()
            if (MediaFileUtil.saveVideoToGallery(context, url)) {
                showFileSaveSuccessToast()
            } else {
                showFileSaveFailedToast()
            }
        }
    }
}