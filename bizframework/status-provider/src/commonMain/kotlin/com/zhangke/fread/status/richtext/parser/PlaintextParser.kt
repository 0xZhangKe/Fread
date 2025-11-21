package com.zhangke.fread.status.richtext.parser

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import com.zhangke.framework.architect.theme.primaryLight
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.FacetFeatureUnion
import com.zhangke.fread.status.richtext.OnLinkTargetClick
import com.zhangke.fread.status.richtext.model.RichLinkTarget
import okio.utf8Size

object PlaintextParser {

    fun parse(
        document: String,
        facets: List<Facet>,
        onLinkTargetClick: OnLinkTargetClick,
    ): AnnotatedString {
        val annotatedStringBuilder = AnnotatedString.Builder()
        annotatedStringBuilder.append(document)
        val legalRange = 0..document.length
        for (facet in facets) {
            val start = calculateUtf8Index(facet.byteStart, document)
            val end = calculateUtf8Index(facet.byteEnd, document)
            if (start > end) continue
            if (!legalRange.contains(start) || !legalRange.contains(end)) {
                continue
            }
            for (feature in facet.features) {
                var linkTarget: RichLinkTarget? = null
                var linkTag: String? = null
                when (feature) {
                    is FacetFeatureUnion.Mention -> {
                        linkTag = feature.did
                        linkTarget = RichLinkTarget.MentionDidTarget(feature.did)
                    }

                    is FacetFeatureUnion.Link -> {
                        linkTag = feature.uri
                        linkTarget = RichLinkTarget.UrlTarget(feature.uri)
                    }

                    is FacetFeatureUnion.Tag -> {
                        linkTag = feature.tag
                        linkTarget = RichLinkTarget.MaybeHashtagTarget(feature.tag)
                    }
                }
                annotatedStringBuilder.addLink(
                    clickable = LinkAnnotation.Clickable(
                        tag = linkTag,
                        styles = TextLinkStyles(
                            style = SpanStyle(color = primaryLight),
                            hoveredStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                        ),
                        linkInteractionListener = {
                            onLinkTargetClick(linkTarget)
                        },
                    ),
                    start = start.toInt(),
                    end = end.toInt(),
                )
            }
        }
        return annotatedStringBuilder.toAnnotatedString()
    }

    private fun calculateUtf8Index(
        index: Long,
        text: String,
    ): Long {
        if (index > text.length) {
            return text.length.toLong()
        }
        val utf8Index = text.utf8Size(0, index.toInt())
        if (utf8Index == index) return index
        val diff = utf8Index - index
        if (diff > 0) {
            return index - diff
        }
        return index
    }
}
