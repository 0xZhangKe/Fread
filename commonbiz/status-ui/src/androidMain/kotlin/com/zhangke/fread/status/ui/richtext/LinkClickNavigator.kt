package com.zhangke.fread.status.ui.richtext

import android.content.Context
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention

interface LinkClickNavigator {

    fun onUrlClick(context: Context, url: String)

    fun onMentionClick(statusUiState: StatusUiState, mention: Mention)

    fun onHashtagClick(statusUiState: StatusUiState, hashtag: HashtagInStatus)
}

abstract class CommonLinkClickNavigator : LinkClickNavigator {

    override fun onUrlClick(context: Context, url: String) {
        BrowserLauncher.launchWebTabInApp(context, url)
    }
}
