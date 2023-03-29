package com.zhangke.utopia.pages.feeds

import com.zhangke.utopia.pages.UtopiaRouters

val UtopiaRouters.Feeds: FeedsRouters
    get() = FeedsRouters(this)

class FeedsRouters(utopiaRouters: UtopiaRouters) {

    val root = "${utopiaRouters.root}/feeds"

    val container: String = "$root/container"
}