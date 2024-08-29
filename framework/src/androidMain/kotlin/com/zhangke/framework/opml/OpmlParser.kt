package com.zhangke.framework.opml

import com.zhangke.framework.ktx.ifNullOrEmpty
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

object OpmlParser {

    fun parse(xml: String): List<OpmlOutline> {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))
        val outlineList = mutableListOf<Outline>()
        val outlineStack = mutableListOf<Outline>()
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "outline") {
                        val text = parser.getAttributeValue(null, "text")
                        val title = parser.getAttributeValue(null, "title")
                        val xmlUrl = parser.getAttributeValue(null, "xmlUrl")
                        val htmlUrl = parser.getAttributeValue(null, "htmlUrl")
                        val outline = Outline(
                            title = title.orEmpty(),
                            text = text.orEmpty(),
                            xmlUrl = xmlUrl.orEmpty(),
                            htmlUrl = htmlUrl.orEmpty(),
                            children = mutableListOf(),
                        )
                        if (outlineStack.isEmpty()) {
                            outlineList.add(outline)
                        } else {
                            outlineStack.last().children.add(outline)
                        }
                        outlineStack.add(outline)
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "outline") {
                        outlineStack.removeAt(outlineStack.size - 1)
                    }
                }
            }
            eventType = try {
                parser.next()
            } catch (e: Throwable) {
                XmlPullParser.END_DOCUMENT
            }
        }
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

    class Outline(
        val title: String,
        val text: String,
        val xmlUrl: String,
        val htmlUrl: String?,
        val children: MutableList<Outline>,
    )
}
