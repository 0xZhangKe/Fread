package com.zhangke.fread.common.utils

import com.zhangke.framework.media.MediaFileUtil
import com.zhangke.framework.toast.showFileSaveFailedToast
import com.zhangke.framework.toast.showFileSaveSuccessToast
import com.zhangke.framework.toast.showFileSavingToast
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class MediaFileHelper @Inject constructor(
    private val context: ApplicationContext,
) {
    actual suspend fun saveImageToGallery(url: String) {
        showFileSavingToast()
        if (MediaFileUtil.saveImageToGallery(context, url)) {
            showFileSaveSuccessToast()
        } else {
            showFileSaveFailedToast()
        }
    }
}