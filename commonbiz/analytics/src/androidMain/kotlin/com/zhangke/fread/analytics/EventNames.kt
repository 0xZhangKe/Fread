package com.zhangke.fread.analytics

import android.os.Bundle

object EventNames {

    const val PAGE_SHOW = "page_show"
    const val CLICK = "click"
    const val INFO = "info"
}

object EventParamsName {

    const val PAGE_NAME = "page_name"
    const val ELEMENT = "element"
}

fun Bundle.putPageName(pageName: String) {
    putString(EventParamsName.PAGE_NAME, pageName)
}

fun Bundle.putElement(element: String) {
    putString(EventParamsName.ELEMENT, element)
}
