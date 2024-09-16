package com.zhangke.framework.opml

data class OpmlOutline(
    val title: String,
    val xmlUrl: String,
    val children: List<OpmlOutline>,
)
