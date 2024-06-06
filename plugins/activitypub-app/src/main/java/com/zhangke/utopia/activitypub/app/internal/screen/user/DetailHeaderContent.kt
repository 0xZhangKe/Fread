package com.zhangke.utopia.activitypub.app.internal.screen.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.ui.richtext.UtopiaRichText

@Composable
fun DetailHeaderContent(
    progress: Float,
    loading: Boolean,
    banner: String?,
    avatar: String?,
    title: String?,
    description: String?,
    acctLine: @Composable () -> Unit,
    followInfo: @Composable () -> Unit,
    onBannerClick: () -> Unit,
    onAvatarClick: () -> Unit,
    relationship: RelationshipUiState? = null,
    emojis: List<Emoji>? = null,
    onUnblockClick: (() -> Unit)? = null,
    onCancelFollowRequestClick: (() -> Unit)? = null,
    onAcceptClick: (() -> Unit)? = null,
    onRejectClick: (() -> Unit)? = null,
    onFollowAccountClick: (() -> Unit)? = null,
    onUnfollowAccountClick: (() -> Unit)? = null,
) {
    val bannerHeight = 180.dp
    val avatarSize = 80.dp
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val (bannerRef, avatarRef, relationBtnRef, nameRef) = createRefs()
        val (acctRef, noteRef, followRef) = createRefs()
        Banner(
            modifier = Modifier
                .clickable { onBannerClick() }
                .constrainAs(bannerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(bannerHeight)
                },
            url = banner,
        )

        // avatar
        AsyncImage(
            modifier = Modifier
                .scale(1F - progress)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .clickable { onAvatarClick() }
                .utopiaPlaceholder(loading)
                .constrainAs(avatarRef) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(bannerRef.bottom)
                    bottom.linkTo(bannerRef.bottom)
                    width = Dimension.value(avatarSize)
                    height = Dimension.value(avatarSize)
                },
            model = avatar,
            contentScale = ContentScale.Crop,
            contentDescription = "avatar",
        )

        // relationship button
        if (relationship == null) {
            Box(
                modifier = Modifier.constrainAs(relationBtnRef) {
                    top.linkTo(bannerRef.bottom, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.value(0.dp)
                }
            )
        } else {
            RelationshipStateButton(
                modifier = Modifier.constrainAs(relationBtnRef) {
                    top.linkTo(bannerRef.bottom, 8.dp)
                    end.linkTo(parent.end, 16.dp)
                },
                relationship = relationship,
                onFollowClick = onFollowAccountClick ?: {},
                onUnfollowClick = onUnfollowAccountClick ?: {},
                onAcceptClick = onAcceptClick ?: {},
                onRejectClick = onRejectClick ?: {},
                onCancelFollowRequestClick = onCancelFollowRequestClick ?: {},
                onUnblockClick = onUnblockClick ?: {},
            )
        }

        // title
        Text(
            modifier = Modifier
                .utopiaPlaceholder(loading)
                .constrainAs(nameRef) {
                    top.linkTo(avatarRef.bottom, 16.dp)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
            textAlign = TextAlign.Start,
            text = title.orEmpty(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp,
        )

        // acct line
        Box(
            modifier = Modifier
                .widthIn(min = 36.dp)
                .utopiaPlaceholder(loading)
                .constrainAs(acctRef) {
                    top.linkTo(nameRef.bottom, 6.dp)
                    start.linkTo(nameRef.start)
                    width = Dimension.wrapContent
                },
            contentAlignment = Alignment.CenterStart,
        ) {
            acctLine()
        }

        // description
        UtopiaRichText(
            modifier = Modifier
                .utopiaPlaceholder(loading)
                .fillMaxWidth()
                .constrainAs(noteRef) {
                    top.linkTo(acctRef.bottom, 6.dp)
                    start.linkTo(nameRef.start)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
            content = description.orEmpty(),
            onMentionClick = {},
            onHashtagClick = {},
            mentions = emptyList(),
            emojis = emojis.orEmpty(),
            tags = emptyList(),
        )

        // follow info line
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .utopiaPlaceholder(loading)
                .constrainAs(followRef) {
                    top.linkTo(noteRef.bottom)
                    start.linkTo(noteRef.start)
                    width = Dimension.wrapContent
                },
        ) {
            followInfo()
        }
    }
}

@Composable
private fun Banner(
    modifier: Modifier,
    url: String?,
) {
    Box(modifier = modifier) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.detail_page_banner_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        val maskColor = MaterialTheme.colorScheme.inverseSurface
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                maskColor.copy(alpha = 0.3F),
                                maskColor.copy(alpha = 0F),
                            ),
                        ),
                    )
                },
            model = url,
            contentScale = ContentScale.Crop,
            contentDescription = "banner",
        )
    }
}
