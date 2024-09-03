package com.zhangke.fread.status.ui.source

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageActionPainter
import com.seiko.imageloader.ui.AutoSizeBox
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun SourceCommonUi(
    thumbnail: String,
    title: String,
    subtitle: String?,
    description: String,
    protocolName: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            val (avatarRef, protocolRef, subtitleRef, nameRef, descRef) = createRefs()
            AutoSizeBox(
                thumbnail,
                modifier = Modifier
                    .constrainAs(avatarRef) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(parent.top, 8.dp)
                        width = Dimension.value(48.dp)
                        height = Dimension.value(48.dp)
                    },
            ) { action ->
                Image(
                    rememberImageActionPainter(action),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .freadPlaceholder(action is ImageAction.Loading)
                        .matchParentSize()
                        .clip(CircleShape),
                )
            }
            Text(
                modifier = Modifier.constrainAs(nameRef) {
                    start.linkTo(avatarRef.end, 8.dp)
                    top.linkTo(parent.top, 6.dp)
                    end.linkTo(protocolRef.start)
                },
                text = title,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier.constrainAs(protocolRef) {
                    start.linkTo(nameRef.end, 6.dp)
                    baseline.linkTo(nameRef.baseline)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
                text = protocolName,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
            )
            if (subtitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier
                    .height(0.5.dp)
                    .constrainAs(subtitleRef) {
                        start.linkTo(nameRef.start)
                        top.linkTo(nameRef.bottom)
                        end.linkTo(nameRef.end)
                        width = Dimension.fillToConstraints
                    })
            } else {
                Text(
                    modifier = Modifier.constrainAs(subtitleRef) {
                        start.linkTo(nameRef.start)
                        top.linkTo(nameRef.bottom, 2.dp)
                        end.linkTo(nameRef.end)
                        width = Dimension.fillToConstraints
                    },
                    text = subtitle,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            FreadRichText(
                modifier = Modifier.constrainAs(descRef) {
                    start.linkTo(nameRef.start)
                    top.linkTo(subtitleRef.bottom, 2.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
                content = description,
                maxLines = 3,
                emojis = emptyList(),
                mentions = emptyList(),
                tags = emptyList(),
                onMentionClick = {},
                onHashtagClick = {},
                onUrlClick = {
                    BrowserLauncher.launchWebTabInApp(context, it)
                },
            )
        }
        if (showDivider) {
            HorizontalDivider()
        }
    }
}
