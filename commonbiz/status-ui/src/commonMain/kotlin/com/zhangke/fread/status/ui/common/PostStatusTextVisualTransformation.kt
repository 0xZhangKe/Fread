package com.zhangke.fread.status.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.zhangke.fread.common.utils.HashtagTextUtils
import com.zhangke.fread.common.utils.LinkTextUtils
import com.zhangke.fread.common.utils.MentionTextUtil

class PostStatusTextVisualTransformation(
    private val highLightColor: Color,
    private val enableMentions: Boolean = true,
    private val allowHashtagInHashtag: Boolean = false,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val hashtags = HashtagTextUtils.findHashtags(text.text, allowHashtagInHashtag)
        val mentions =
            if (enableMentions) MentionTextUtil.findMentionList(text.text) else emptyList()
        val links = LinkTextUtils.findLinks(text.text)
        val highlightList = hashtags + mentions + links
        return TransformedText(
            text = buildAnnotatedString {
                append(text)
                highlightList.forEach {
                    addStyle(
                        style = SpanStyle(
                            color = highLightColor,
                        ),
                        start = it.start,
                        end = it.end,
                    )
                }
            },
            offsetMapping = OffsetMapping.Identity,
        )
    }
}
