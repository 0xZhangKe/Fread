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

class FacetHtmlParser {

    fun parse(
        document: String,
        facets: List<Facet>,
        onLinkTargetClick: OnLinkTargetClick,
    ): AnnotatedString {
        val annotatedStringBuilder = AnnotatedString.Builder()
        val chars = document.toCharArray()
        val legalRange = chars.indices
        for (facet in facets) {
            val start = facet.byteStart
            val end = facet.byteEnd
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
                annotatedStringBuilder.pushLink(
                    LinkAnnotation.Clickable(
                        tag = linkTag,
                        styles = TextLinkStyles(
                            style = SpanStyle(color = primaryLight),
                            hoveredStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                        ),
                        linkInteractionListener = {
                            onLinkTargetClick(linkTarget)
                        },
                    )
                )
            }
        }
        return annotatedStringBuilder.toAnnotatedString()
    }
}
