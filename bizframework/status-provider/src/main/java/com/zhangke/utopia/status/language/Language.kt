package com.zhangke.utopia.status.language

import java.util.Locale
import java.util.Locale.LanguageRange

data class Language(
    val name: String,
){

    init {
        Locale.CHINESE
        Locale.getDefault()
    }
}
