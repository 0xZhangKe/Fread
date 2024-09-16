package com.zhangke.fread.analytics

import android.os.Bundle

fun Bundle.putPageName(pageName: String) {
    putString(EventParamsName.PAGE_NAME, pageName)
}

fun Bundle.putElement(element: String) {
    putString(EventParamsName.ELEMENT, element)
}
