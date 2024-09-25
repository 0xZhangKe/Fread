package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import androidx.compose.runtime.Composable
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_context_account
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_context_home
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_context_notification
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_context_thread
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_context_timeline
import org.jetbrains.compose.resources.stringResource

enum class FilterContext(val contextName: String) {

    HOME_LIST("home"),
    NOTIFICATION("notifications"),
    TIMELINE("public"),
    POST_REPLY("thread"),
    USER_INFO("account");

    val title: String
        @Composable get() = when (this) {
            HOME_LIST -> stringResource(Res.string.activity_pub_filter_edit_context_home)
            NOTIFICATION -> stringResource(Res.string.activity_pub_filter_edit_context_notification)
            TIMELINE -> stringResource(Res.string.activity_pub_filter_edit_context_timeline)
            POST_REPLY -> stringResource(Res.string.activity_pub_filter_edit_context_thread)
            USER_INFO -> stringResource(Res.string.activity_pub_filter_edit_context_account)
        }

    companion object {

        fun fromContext(context: String): FilterContext? {
            return FilterContext.entries.firstOrNull { it.contextName == context }
        }
    }
}
