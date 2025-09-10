package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import androidx.compose.runtime.Composable
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

enum class FilterContext(val contextName: String) {

    HOME_LIST("home"),
    NOTIFICATION("notifications"),
    TIMELINE("public"),
    POST_REPLY("thread"),
    USER_INFO("account");

    val title: String
        @Composable get() = when (this) {
            HOME_LIST -> stringResource(LocalizedString.activity_pub_filter_edit_context_home)
            NOTIFICATION -> stringResource(LocalizedString.activity_pub_filter_edit_context_notification)
            TIMELINE -> stringResource(LocalizedString.activity_pub_filter_edit_context_timeline)
            POST_REPLY -> stringResource(LocalizedString.activity_pub_filter_edit_context_thread)
            USER_INFO -> stringResource(LocalizedString.activity_pub_filter_edit_context_account)
        }

    companion object {

        fun fromContext(context: String): FilterContext? {
            return FilterContext.entries.firstOrNull { it.contextName == context }
        }
    }
}
