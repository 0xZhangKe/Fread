package com.zhangke.framework.toast

import android.os.Build
import android.widget.Toast
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.localization.Res
import com.zhangke.fread.localization.image_save_failed
import com.zhangke.fread.localization.image_save_success
import com.zhangke.fread.localization.image_saving
import org.jetbrains.compose.resources.getString

actual fun toast(message: String?) {
    toast(message, Toast.LENGTH_SHORT)
}

fun toast(message: String?, length: Int) {
    if (message.isNullOrEmpty()) return
    Toast.makeText(appContext, message, length).show()
}

private var savingToast: Toast? = null

suspend fun showFileSavingToast() {
    val toast = Toast.makeText(appContext, getString(Res.string.image_saving), Toast.LENGTH_SHORT)
    toast.show()
    savingToast = toast
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        var callback: Toast.Callback? = null
        callback = object : Toast.Callback() {
            override fun onToastHidden() {
                super.onToastHidden()
                callback?.let { toast.removeCallback(it) }
                savingToast = null
            }
        }
        toast.addCallback(callback)
    }
}

suspend fun showFileSaveSuccessToast() {
    savingToast?.cancel()
    savingToast = null
    Toast.makeText(appContext, getString(Res.string.image_save_success), Toast.LENGTH_SHORT).show()
}

suspend fun showFileSaveFailedToast() {
    savingToast?.cancel()
    savingToast = null
    Toast.makeText(appContext, getString(Res.string.image_save_failed), Toast.LENGTH_SHORT).show()
}
