package com.zhangke.utopia.status.ui.richtext

import moe.tlaster.ktml.Ktml
import moe.tlaster.ktml.dom.Element


class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val preProcess: (String) -> String = { it },
    private val postProcess: (Element) -> Element = { it },
) {

    private var element: Element? = null

    fun parseElement(): Element {
        element?.let { return it }
        val doc = preProcess(document)
        val ele = Ktml.parse(doc).let(postProcess)
        element = ele
        return ele
    }
}
