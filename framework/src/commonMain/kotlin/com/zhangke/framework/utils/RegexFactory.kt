package com.zhangke.framework.utils

object RegexFactory {

    fun getDomainRegex(): Regex {
        return "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$"
            .toRegex()
    }
}
