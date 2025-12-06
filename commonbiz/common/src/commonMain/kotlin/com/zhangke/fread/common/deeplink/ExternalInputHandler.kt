package com.zhangke.fread.common.deeplink

import com.zhangke.framework.network.SimpleUri
import com.zhangke.fread.common.action.ComposableActions
import com.zhangke.fread.common.action.RouteAction
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.delay

object ExternalInputHandler {

    suspend fun handle(text: String) {
        delay(500) // delay for waiting page resumed
        val fixedText = ExternalInputParser.parseExternalText(text)
        val uri = SimpleUri.parse(fixedText)
        if (uri == null || uri.host.isNullOrEmpty()) {
            // goto publish screen
            GlobalScreenNavigation.navigateByTransparent(SelectAccountForPublishScreen(fixedText))
        } else {
            val action = KRouter.route<RouteAction>(fixedText)
            if (action?.execute() == true) return
            ComposableActions.post(fixedText)
        }
    }
}
