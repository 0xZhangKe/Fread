package com.zhangke.utopia.feeds.pages.manager.add

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.zhangke.framework.utils.extractActivity
import com.zhangke.utopia.feeds.R

fun showAddContentSuccessToast(context: Context) {
    val activityContext = context.extractActivity() ?: context
    val toast = Toast.makeText(
        activityContext,
        context.getString(R.string.add_content_success_snackbar),
        Toast.LENGTH_SHORT,
    )
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}
