package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.utopia.status.platform.BlogPlatform

@Composable
fun BlogPlatformUi(
    modifier: Modifier,
    platform: BlogPlatform,
) {
    Column(modifier = modifier) {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)) {
            val (avatarRef, protocolRef, domainRef, nameRef, descRef) = createRefs()
            var loadSuccess by remember {
                mutableStateOf(false)
            }
            AsyncImage(
                modifier = Modifier
                    .constrainAs(avatarRef) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(parent.top, 8.dp)
                        width = Dimension.value(48.dp)
                        height = Dimension.value(48.dp)
                    }
                    .clip(CircleShape)
                    .utopiaPlaceholder(!loadSuccess),
                onState = {
                    loadSuccess = it is AsyncImagePainter.State.Success
                },
                model = platform.thumbnail,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.constrainAs(nameRef) {
                    start.linkTo(avatarRef.end, 8.dp)
                    top.linkTo(parent.top, 6.dp)
                },
                text = platform.name,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier.constrainAs(protocolRef) {
                    start.linkTo(nameRef.end, 6.dp)
                    baseline.linkTo(nameRef.baseline)
                },
                text = platform.protocol.name,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                modifier = Modifier.constrainAs(domainRef) {
                    start.linkTo(nameRef.start)
                    top.linkTo(nameRef.bottom, 2.dp)
                    end.linkTo(nameRef.end)
                    width = Dimension.fillToConstraints
                },
                text = platform.baseUrl.host,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                modifier = Modifier.constrainAs(descRef) {
                    start.linkTo(nameRef.start)
                    top.linkTo(domainRef.bottom, 2.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
                text = platform.description,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        HorizontalDivider()
    }
}
