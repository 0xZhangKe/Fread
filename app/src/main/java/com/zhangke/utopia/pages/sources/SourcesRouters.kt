package com.zhangke.utopia.pages.sources

import com.zhangke.utopia.pages.UtopiaRouters

val UtopiaRouters.Sources: SourcesRouters
    get() = SourcesRouters(this)

class SourcesRouters(routers: UtopiaRouters) {

    val root = routers.root.plus("/sources")

    val detail = root.plus("/detail")

    val add = root.plus("/add")
}