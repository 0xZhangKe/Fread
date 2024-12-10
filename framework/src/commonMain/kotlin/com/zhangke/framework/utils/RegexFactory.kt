package com.zhangke.framework.utils

object RegexFactory {

    val domainRegex =
        "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$".toRegex()

    val didRegex = "^did:[a-z0-9]+:[a-zA-Z0-9._%-]+$".toRegex()
}
