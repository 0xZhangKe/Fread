package com.zhangke.fread.feeds.pages.manager.add

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.zhangke.framework.utils.extractActivity
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.add_content_success_snackbar
import org.jetbrains.compose.resources.getString

suspend fun showAddContentSuccessToast(context: Context) {
    val activityContext = context.extractActivity() ?: context
    val toast = Toast.makeText(
        activityContext,
        getString(Res.string.add_content_success_snackbar),
        Toast.LENGTH_SHORT,
    )
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}
