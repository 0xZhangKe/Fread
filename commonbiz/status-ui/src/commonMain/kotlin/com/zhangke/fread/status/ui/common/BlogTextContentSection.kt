package com.zhangke.fread.status.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.isRss
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.ui.model.BlogUIType
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusStyle.ContentStyle
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlogTextContentSection(
    blog: Blog,
    style: ContentStyle,
    type: BlogUIType,
    blogTranslationState: BlogTranslationUiState? = null,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit = {},
    onMaybeHashtagClick: (String) -> Unit = {},
    onMentionClick: (Mention) -> Unit = {},
    onMentionDidClick: (String) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
) {
    val contentMaxLine: Int = if (blog.platform.protocol.isRss) {
        style.maxLine
    } else {
        Int.MAX_VALUE
    }
    val showWarning = blog.spoilerText.isNotEmpty() || blog.sensitiveByFilter
    val spoilerText = blog.spoilerText
    if (showWarning) {
        val statusConfig = LocalStatusUiConfig.current
        var hideContent by rememberSaveable(
            showWarning,
            spoilerText,
            statusConfig.alwaysShowSensitiveContent,
        ) {
            mutableStateOf(!statusConfig.alwaysShowSensitiveContent)
        }
        val humanizedSpoilerText = if (blogTranslationState?.showingTranslation == true) {
            blogTranslationState.blogTranslation!!.getHumanizedSpoilerText(blog)
        } else if (blog.spoilerText.isNotEmpty()) {
            blog.humanizedSpoilerText
        } else {
            val text =
                stringResource(
                    LocalizedString.statusUiSensitiveByFilter,
                    blog.filtered!!.first().title,
                )
            remember { buildRichText(document = text, type = blog.richTextType) }
        }
        SpoilerText(
            modifier = Modifier,
            hideContent = hideContent,
            spoilerText = humanizedSpoilerText,
            fontSize = style.contentSize,
            onShowContent = { hideContent = false },
            onHideContent = { hideContent = true },
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
            onUrlClick = onUrlClick,
        )
        if (blog.content.isNotEmpty()) {
            AnimatedVisibility(
                visible = !hideContent,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                val humanizedContent = if (blogTranslationState?.showingTranslation == true) {
                    blogTranslationState.blogTranslation!!.getHumanizedContent(blog)
                } else {
                    blog.humanizedContent
                }
                BlogRichTextContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .wrapContentHeight(),
                    content = humanizedContent,
                    style = style,
                    type = type,
                    onHashtagInStatusClick = onHashtagInStatusClick,
                    onMentionClick = onMentionClick,
                    onMentionDidClick = onMentionDidClick,
                    onMaybeHashtagClick = onMaybeHashtagClick,
                    onUrlClick = onUrlClick,
                )
            }
        }
    } else {
        if (!blog.title.isNullOrEmpty()) {
            Text(
                modifier = Modifier,
                text = blog.title!!,
                fontWeight = FontWeight.Bold,
                fontSize = style.titleSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (!blog.description.isNullOrEmpty()) {
            val topPadding = if (blog.title.isNullOrEmpty()) {
                style.contentVerticalSpacing
            } else {
                style.contentVerticalSpacing / 2
            }
            FreadRichText(
                modifier = Modifier
                    .padding(top = topPadding),
                richText = blog.humanizedDescription,
                maxLines = contentMaxLine,
                onMentionClick = onMentionClick,
                onMentionDidClick = onMentionDidClick,
                onHashtagClick = onHashtagInStatusClick,
                onMaybeHashtagClick = onMaybeHashtagClick,
                onUrlClick = onUrlClick,
                fontSize = style.contentSize,
            )
            BlogRichTextContent(
                modifier = Modifier
                    .padding(top = topPadding),
                content = blog.humanizedDescription,
                style = style,
                type = type,
                onHashtagInStatusClick = onHashtagInStatusClick,
                onMentionClick = onMentionClick,
                onMentionDidClick = onMentionDidClick,
                onMaybeHashtagClick = onMaybeHashtagClick,
                onUrlClick = onUrlClick,
            )
        } else if (
            blog.title.isNullOrEmpty() &&
            blog.description.isNullOrEmpty() &&
            blog.content.isNotEmpty()
        ) {
            val humanizedContent = if (blogTranslationState?.showingTranslation == true) {
                blogTranslationState.blogTranslation!!.getHumanizedContent(blog)
            } else {
                blog.humanizedContent
            }
            BlogRichTextContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                content = humanizedContent,
                style = style,
                type = type,
                onHashtagInStatusClick = onHashtagInStatusClick,
                onMentionClick = onMentionClick,
                onMentionDidClick = onMentionDidClick,
                onMaybeHashtagClick = onMaybeHashtagClick,
                onUrlClick = onUrlClick,
            )
        }
    }
}

@Composable
private fun BlogRichTextContent(
    modifier: Modifier,
    content: RichText,
    style: ContentStyle,
    type: BlogUIType,
    onMaybeHashtagClick: (String) -> Unit,
    onMentionClick: (Mention) -> Unit,
    onMentionDidClick: (String) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onUrlClick: (url: String) -> Unit,
) {
    var expanded by remember(type) { mutableStateOf(type == BlogUIType.DETAIL) }
    var isOverflow by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        FreadRichText(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            richText = content,
            maxLines = if (expanded) Int.MAX_VALUE else 10,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
            onHashtagClick = onHashtagInStatusClick,
            onMaybeHashtagClick = onMaybeHashtagClick,
            fontSize = style.contentSize,
            onUrlClick = onUrlClick,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = {
                if (it.hasVisualOverflow) {
                    isOverflow = true
                }
            },
        )
        if (isOverflow) {
            ShowMoreOrHideLabel(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                style = style,
                onShowMoreClick = { expanded = true },
                onHideClick = { expanded = false },
            )
        }
    }
}

@Composable
private fun ShowMoreOrHideLabel(
    modifier: Modifier,
    style: ContentStyle,
    expanded: Boolean,
    onShowMoreClick: () -> Unit,
    onHideClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
        TextButton(
            modifier = Modifier,
            onClick = {
                if (expanded) {
                    onHideClick()
                } else {
                    onShowMoreClick()
                }
            },
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        if (expanded) {
                            onHideClick()
                        } else {
                            onShowMoreClick()
                        }
                    },
                fontSize = 12.sp,
                text = if (expanded) {
                    stringResource(LocalizedString.collapse)
                } else {
                    stringResource(LocalizedString.showFullText)
                },
            )
        }
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
    }
}
