package com.zhangke.fread.analytics

import android.os.Bundle

object EventNames {

    const val pageShow = "page_show"
}

object EventParamsName {

    const val pageName = "page_name"
}

fun Bundle.putPageName(pageName: String) {
    putString(EventParamsName.pageName, pageName)
}
