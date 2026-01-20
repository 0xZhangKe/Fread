package com.zhangke.fread.common.utils

import android.content.Context
import android.content.Intent
import androidx.core.text.HtmlCompat
import com.zhangke.framework.utils.startActivityCompat

object ShareHelper {

    fun shareUrl(
        context: Context,
        url: String,
        text: String,
    ) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())
            putExtra(Intent.EXTRA_TEXT, url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivityCompat(Intent.createChooser(intent, null))
    }
}
