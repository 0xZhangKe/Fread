package com.zhangke.fread.status.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
    Column(modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(author) }
                .padding(bottom = 8.dp)
        ) {
            val (avatarRef, nameRef, webFingerRef, descRef) = createRefs()
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(StatusInfoStyleDefaults.avatarSize)
                    .constrainAs(avatarRef) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(parent.top, 8.dp)
                    },
                imageUrl = author.avatar,
            )
            FreadRichText(
                modifier = Modifier.constrainAs(nameRef) {
                    start.linkTo(avatarRef.end, 8.dp)
                    top.linkTo(avatarRef.top)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
                richText = author.humanizedName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSizeSp = 16F,
                onUrlClick = onUrlClick,
            )
            Text(
                modifier = Modifier.constrainAs(webFingerRef) {
                    start.linkTo(nameRef.start)
                    top.linkTo(nameRef.bottom, 2.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
                textAlign = TextAlign.Start,
                text = author.webFinger.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
            )
            FreadRichText(
                modifier = Modifier.constrainAs(descRef) {
                    start.linkTo(nameRef.start)
                    top.linkTo(webFingerRef.bottom, 2.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
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
        HorizontalDivider()
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
    val browserLauncher = LocalActivityBrowserLauncher.current
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { composedStatusInteraction.onUserInfoClick(role, author) }
            .padding(bottom = 8.dp)
    ) {
        val (avatarRef, nameRef, webFingerRef, descRef, followBtn) = createRefs()
        BlogAuthorAvatar(
            modifier = Modifier
                .size(StatusInfoStyleDefaults.avatarSize)
                .constrainAs(avatarRef) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top, 8.dp)
                },
            imageUrl = author.avatar,
        )
        FreadRichText(
            modifier = Modifier.constrainAs(nameRef) {
                start.linkTo(avatarRef.end, 8.dp)
                top.linkTo(avatarRef.top)
                end.linkTo(followBtn.start, 8.dp)
                width = Dimension.fillToConstraints
            },
            richText = author.humanizedName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSizeSp = 16F,
            onUrlClick = {
                browserLauncher.launchWebTabInApp(it, role)
            },
        )
        Text(
            modifier = Modifier.constrainAs(webFingerRef) {
                start.linkTo(nameRef.start)
                top.linkTo(nameRef.bottom, 2.dp)
                end.linkTo(followBtn.start, 8.dp)
                width = Dimension.fillToConstraints
            },
            textAlign = TextAlign.Start,
            text = author.webFinger.toString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
        )
        FreadRichText(
            modifier = Modifier.constrainAs(descRef) {
                start.linkTo(nameRef.start)
                top.linkTo(webFingerRef.bottom, 4.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
            },
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
        StyledTextButton(
            modifier = Modifier
                .constrainAs(followBtn) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top, 8.dp)
                },
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
}
