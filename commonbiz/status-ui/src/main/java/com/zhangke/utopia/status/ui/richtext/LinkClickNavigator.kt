package com.zhangke.utopia.status.ui.richtext

import android.content.Context
import com.zhangke.framework.browser.BrowserLauncher
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention

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
