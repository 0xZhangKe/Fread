package com.zhangke.framework.media

import android.content.Context
import com.zhangke.framework.toast.showFileSaveFailedToast
import com.zhangke.framework.toast.showFileSaveSuccessToast
import com.zhangke.framework.toast.showFileSavingToast

object MediaFileHelper {

    suspend fun saveImageToGallery(context: Context, url: String){
        showFileSavingToast()
        if (MediaFileUtil.saveImageToGallery(context, url)) {
            showFileSaveSuccessToast()
        } else {
            showFileSaveFailedToast()
        }
    }
}
