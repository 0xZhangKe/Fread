package com.zhangke.fread.status.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusInfoStyleDefaults
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_follow
import com.zhangke.fread.statusui.status_ui_unfollow
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlogAuthorUi(
    modifier: Modifier,
    author: BlogAuthor,
    onClick: (BlogAuthor) -> Unit,
    onUrlClick: (String) -> Unit,
) {
    Column(modifier = modifier.clickable { onClick(author) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            BlogAuthorAvatar(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
                    .size(StatusInfoStyleDefaults.avatarSize),
                imageUrl = author.avatar,
            )
            Column(
                modifier = Modifier.weight(1F)
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp),
            ) {
                FreadRichText(
                    modifier = Modifier.fillMaxWidth(),
                    richText = author.humanizedName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSizeSp = 16F,
                    onUrlClick = onUrlClick,
                )
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    textAlign = TextAlign.Start,
                    text = author.webFinger.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                )
                FreadRichText(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    content = author.description,
                    emojis = author.emojis,
                    mentions = emptyList(),
                    tags = emptyList(),
                    onHashtagClick = {},
                    onMentionClick = {},
                    maxLines = 1,
                    onUrlClick = onUrlClick,
                )
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
    }
}

@Composable
fun RecommendAuthorUi(
    modifier: Modifier,
    role: IdentityRole,
    author: BlogAuthor,
    following: Boolean,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    BaseBlogAuthor(
        modifier = modifier,
        role = role,
        author = author,
        following = following,
        composedStatusInteraction = composedStatusInteraction,
    )
}

@Composable
private fun BaseBlogAuthor(
    modifier: Modifier,
    role: IdentityRole,
    author: BlogAuthor,
    following: Boolean,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    Box(
        modifier = modifier.fillMaxWidth()
            .clickable { composedStatusInteraction.onUserInfoClick(role, author) },
    ) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            BlogAuthorAvatar(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
                    .size(StatusInfoStyleDefaults.avatarSize),
                imageUrl = author.avatar,
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 2.dp, end = 8.dp)
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1F).align(Alignment.CenterVertically)) {
                        FreadRichText(
                            modifier = Modifier.fillMaxWidth(),
                            richText = author.humanizedName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSizeSp = 16F,
                            onUrlClick = {
                                browserLauncher.launchWebTabInApp(it, role)
                            },
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            textAlign = TextAlign.Start,
                            text = author.webFinger.toString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    StyledTextButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = if (following) {
                            stringResource(Res.string.status_ui_unfollow)
                        } else {
                            stringResource(Res.string.status_ui_follow)
                        },
                        style = TextButtonStyle.STANDARD,
                        onClick = {
                            if (following) {
                                composedStatusInteraction.onUnfollowClick(role, author)
                            } else {
                                composedStatusInteraction.onFollowClick(role, author)
                            }
                        },
                    )
                }

                FreadRichText(
                    modifier = Modifier
                        .padding(end = 8.dp),
                    content = author.description,
                    emojis = author.emojis,
                    mentions = emptyList(),
                    tags = emptyList(),
                    onMentionClick = {
                        composedStatusInteraction.onMentionClick(role, it)
                    },
                    onHashtagClick = {
                        composedStatusInteraction.onHashtagInStatusClick(role, it)
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    onUrlClick = {
                        browserLauncher.launchWebTabInApp(it, role)
                    },
                )
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}
