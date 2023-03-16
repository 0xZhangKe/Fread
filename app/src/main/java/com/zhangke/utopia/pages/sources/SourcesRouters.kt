package com.zhangke.utopia.pages.sources

import com.zhangke.utopia.pages.UtopiaRouters

val UtopiaRouters.Sources: SourcesRouters
    get() = SourcesRouters(this)

@Suppress("PropertyName")
class SourcesRouters(routers: UtopiaRouters) {

    val Root = routers.Root.plus("/sources")

    val Search = Root.plus("/search")

    val Detail = Root.plus("/detail")
}