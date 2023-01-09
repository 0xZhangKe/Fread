package com.zhangke.framework.utils

object RegexFactory {

    fun getDomainRegex(): Regex {
        return "\\w{1,63}(\\.\\w{1,63})+".toRegex()
    }
}