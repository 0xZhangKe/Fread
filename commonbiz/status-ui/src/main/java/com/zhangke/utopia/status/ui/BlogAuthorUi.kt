package com.zhangke.utopia.status.ui

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
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.ui.richtext.UtopiaRichText
import com.zhangke.utopia.status.ui.style.StatusInfoStyleDefaults

@Composable
fun BlogAuthorUi(
    modifier: Modifier,
    author: BlogAuthor,
    onClick: (BlogAuthor) -> Unit,
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
            Text(
                modifier = Modifier.constrainAs(nameRef) {
                    start.linkTo(avatarRef.end, 8.dp)
                    top.linkTo(avatarRef.top)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
                textAlign = TextAlign.Start,
                text = author.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
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
            UtopiaRichText(
                modifier = Modifier.constrainAs(descRef) {
                    start.linkTo(nameRef.start)
                    top.linkTo(webFingerRef.bottom, 2.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
                content = author.description,
                mentions = emptyList(),
                maxLines = 1,
            )
        }
        HorizontalDivider()
    }
}
