package com.zhangke.utopia.status.ui.richtext.android

import android.text.SpannableStringBuilder
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.Mention
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeVisitor

object HtmlParser {

    fun parse(
        document: String,
        emojis: List<Emoji>,
        mentions: List<Mention>,
        hashTag: List<Hashtag>,
    ): SpannableStringBuilder {
        val spanBuilder = SpannableStringBuilder()
        Jsoup.parseBodyFragment(document)
            .body()
            .traverse(object : NodeVisitor {

                override fun head(node: Node, depth: Int) {
                    if (node is TextNode) {
                        spanBuilder.append(node.text())
                    } else if (node is Element) {
                        parseElement(spanBuilder, node)
                    }
                }

                override fun tail(node: Node, depth: Int) {
                    TODO("Not yet implemented")
                }
            })
        return spanBuilder
    }

    private fun parseElement(
        spanBuilder: SpannableStringBuilder,
        element: Element,
    ) {
        when(element.nodeName()){
            "a" -> {

            }
        }
    }
}
