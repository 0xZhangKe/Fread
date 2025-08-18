package com.zhangke.fread.activitypub.app.internal.screen.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.common.ProgressedAvatar
import com.zhangke.fread.status.ui.common.ProgressedBanner
import com.zhangke.fread.status.ui.richtext.SelectableRichText

@Composable
fun DetailHeaderContent(
    progress: Float,
    loading: Boolean,
    banner: String?,
    avatar: String?,
    title: RichText?,
    description: RichText?,
    acctLine: @Composable () -> Unit,
    followInfo: @Composable () -> Unit,
    onBannerClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onUrlClick: (String) -> Unit,
    onMaybeHashtagClick: (String) -> Unit,
    privateNote: String? = null,
    action: (@Composable () -> Unit)? = null,
    bottomArea: (@Composable () -> Unit)? = null,
) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (bannerRef, avatarRef, relationBtnRef, nameRef) = createRefs()
        val (acctRef, privateNoteRef, noteRef, followRef) = createRefs()
        val bottomAreaRef = createRef()
        ProgressedBanner(
            modifier = Modifier
                .clickable { onBannerClick() }
                .constrainAs(bannerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            url = banner,
        )

        // avatar
        ProgressedAvatar(
            modifier = Modifier
                .constrainAs(avatarRef) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(bannerRef.bottom)
                    bottom.linkTo(bannerRef.bottom)
                },
            avatar = avatar,
            progress = progress,
            loading = loading,
            onAvatarClick = onAvatarClick,
        )

        // relationship button
        if (action == null) {
            Box(
                modifier = Modifier.constrainAs(relationBtnRef) {
                    top.linkTo(bannerRef.bottom, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.value(0.dp)
                }
            )
        } else {
            Box(
                modifier = Modifier.constrainAs(relationBtnRef) {
                    top.linkTo(bannerRef.bottom, 8.dp)
                    end.linkTo(parent.end, 16.dp)
                }
            ) {
                action()
            }
        }

        // title
        SelectableRichText(
            modifier = Modifier
                .freadPlaceholder(loading)
                .constrainAs(nameRef) {
                    top.linkTo(avatarRef.bottom, 8.dp)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
            richText = title ?: RichText.empty,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSizeSp = 18F,
            onUrlClick = onUrlClick,
        )

        // acct line
        Box(
            modifier = Modifier
                .widthIn(min = 36.dp)
                .freadPlaceholder(loading)
                .constrainAs(acctRef) {
                    top.linkTo(nameRef.bottom, 6.dp)
                    start.linkTo(nameRef.start)
                    width = Dimension.wrapContent
                },
            contentAlignment = Alignment.CenterStart,
        ) {
            acctLine()
        }

        // private note
        if (privateNote.isNullOrEmpty()) {
            Box(modifier = Modifier.constrainAs(privateNoteRef) {
                top.linkTo(acctRef.bottom)
                start.linkTo(nameRef.start)
            })
        } else {
            Box(
                modifier = Modifier
                    .constrainAs(privateNoteRef) {
                        top.linkTo(acctRef.bottom, 6.dp)
                        start.linkTo(nameRef.start)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
            ) {
                val privateNoteStr = buildAnnotatedString {
                    val prefix = "NOTE: "
                    append(prefix)
                    append(privateNote)
                }
                SelectionContainer {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        text = privateNoteStr,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        // description
        SelectableRichText(
            modifier = Modifier
                .freadPlaceholder(loading)
                .fillMaxWidth()
                .constrainAs(noteRef) {
                    top.linkTo(privateNoteRef.bottom, 6.dp)
                    start.linkTo(nameRef.start)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
            richText = description ?: RichText.empty,
            onUrlClick = onUrlClick,
            onMaybeHashtagClick = onMaybeHashtagClick,
        )

        // follow info line
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .freadPlaceholder(loading)
                .constrainAs(followRef) {
                    top.linkTo(noteRef.bottom)
                    start.linkTo(noteRef.start)
                    width = Dimension.wrapContent
                },
        ) {
            followInfo()
        }

        // bottom area
        Box(
            modifier = Modifier.constrainAs(bottomAreaRef) {
                top.linkTo(followRef.bottom, 8.dp)
                start.linkTo(followRef.start)
                end.linkTo(parent.end, 16.dp)
                bottom.linkTo(parent.bottom, 8.dp)
                width = Dimension.fillToConstraints
            },
        ) {
            bottomArea?.invoke()
        }
    }
}
