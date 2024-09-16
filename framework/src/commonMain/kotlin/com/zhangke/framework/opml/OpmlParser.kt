package com.zhangke.framework.opml

import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.parser.XmlTreeBuilder
import com.zhangke.framework.ktx.ifNullOrEmpty

object OpmlParser {

    fun parse(xml: String): List<OpmlOutline> {
        val outlineList = mutableListOf<Outline>()
        val outlineStack = mutableListOf<Outline>()

        fun parserNode(node: Node) {
            if (node.nodeName() == "outline") {
                val text = node.attr("text")
                val title = node.attr("title")
                val xmlUrl = node.attr("xmlUrl")
                val htmlUrl = node.attr("htmlUrl")
                val outline = Outline(
                    title = title,
                    text = text,
                    xmlUrl = xmlUrl,
                    htmlUrl = htmlUrl,
                    children = mutableListOf(),
                )
                if (outlineStack.isEmpty()) {
                    outlineList.add(outline)
                } else {
                    outlineStack.last().children.add(outline)
                }
                if (node.childNodes().isNotEmpty()) {
                    outlineStack.add(outline)
                    node.childNodes().forEach {
                        parserNode(it)
                    }
                    outlineStack.removeAt(outlineStack.size - 1)
                }
            } else if (node.childNodes().isNotEmpty()) {
                node.childNodes().forEach {
                    parserNode(it)
                }
            }
        }

        val doc = XmlTreeBuilder().parse(xml)
        parserNode(doc)


        return outlineList.map {
            it.toOpmlOutline()
        }
    }

    private fun Outline.toOpmlOutline(): OpmlOutline {
        return OpmlOutline(
            title = text.ifNullOrEmpty { title },
            xmlUrl = xmlUrl,
            children = children.map { it.toOpmlOutline() },
        )
    }

    private class Outline(
        val title: String,
        val text: String,
        val xmlUrl: String,
        val htmlUrl: String?,
        val children: MutableList<Outline>,
    ) {
        override fun toString(): String {
            return "Outline(title='$title', text='$text', xmlUrl='$xmlUrl', htmlUrl=$htmlUrl, children=$children)"
        }
    }
}
