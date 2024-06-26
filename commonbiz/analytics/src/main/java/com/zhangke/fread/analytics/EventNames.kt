package com.zhangke.fread.analytics

import android.os.Bundle

object EventNames {

    const val pageShow = "page_show"
    const val click = "click"
}

object EventParamsName {

    const val pageName = "page_name"
    const val element = "element"
}

fun Bundle.putPageName(pageName: String) {
    putString(EventParamsName.pageName, pageName)
}

fun Bundle.putElement(element: String) {
    putString(EventParamsName.element, element)
}
