package com.zhangke.framework.media

import android.content.Context
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.toast.showFileSaveFailedToast
import com.zhangke.framework.toast.showFileSaveSuccessToast
import com.zhangke.framework.toast.showFileSavingToast
import kotlinx.coroutines.launch

object MediaFileHelper {

    suspend fun saveImageToGallery(context: Context, url: String) {
        showFileSavingToast()
        if (MediaFileUtil.saveImageToGallery(context, url)) {
            showFileSaveSuccessToast()
        } else {
            showFileSaveFailedToast()
        }
    }

    suspend fun saveVideoToGallery(context: Context, url: String) {
        showFileSavingToast()
        ApplicationScope.launch {
            if (MediaFileUtil.saveVideoToGallery(context, url)) {
                showFileSaveSuccessToast()
            } else {
                showFileSaveFailedToast()
            }
        }
    }
}
