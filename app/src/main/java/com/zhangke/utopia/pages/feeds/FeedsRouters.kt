package com.zhangke.utopia.pages.feeds

import com.zhangke.utopia.pages.UtopiaRouters

val UtopiaRouters.Feeds: FeedsRouters
    get() = FeedsRouters(this)

@Suppress("PropertyName")
class FeedsRouters(utopiaRouters: UtopiaRouters) {

    val Root = "${utopiaRouters.Root}/feeds"

    val List: String get() = "$Root/list"
}