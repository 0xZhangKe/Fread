package com.zhangke.fread.status.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.zhangke.fread.framework.R
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun CardInfoSection(
    modifier: Modifier,
    avatar: String?,
    title: String,
    description: String?,
    onUrlClick: (String) -> Unit,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    Card(
        modifier = modifier,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (avatarRef, contentRef, actionsRef) = createRefs()
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .constrainAs(avatarRef) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(contentRef.start)
                        top.linkTo(parent.top, 12.dp)
                    },
                placeholder = painterResource(R.drawable.ic_avatar),
                error = painterResource(R.drawable.ic_avatar),
                model = avatar,
                contentDescription = "Avatar",
            )
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(contentRef) {
                        start.linkTo(avatarRef.end, 16.dp)
                        top.linkTo(parent.top, 12.dp)
                        end.linkTo(actionsRef.start)
                        bottom.linkTo(parent.bottom, 12.dp)
                        width = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    maxLines = 1,
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (description.isNullOrEmpty().not()) {
                    FreadRichText(
                        modifier = Modifier.padding(top = 2.dp),
                        content = description!!,
                        mentions = emptyList(),
                        emojis = emptyList(),
                        tags = emptyList(),
                        onHashtagClick = {},
                        onMentionClick = {},
                        onUrlClick = onUrlClick,
                        maxLines = 3,
                    )
                }
            }
            Box(modifier = Modifier.constrainAs(actionsRef) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }) {
                if (actions != null) {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 12.dp, end = 16.dp)
                    ) {
                        actions()
                    }
                }
            }
        }
    }
}
