package com.zhangke.fread.activitypub.app.internal.screen.user.common.users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun CommonUserUi(
    user: BlogAuthor,
    modifier: Modifier = Modifier,
    actionButton: @Composable RowScope.() -> Unit = {},
    showDivider: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            BlogAuthorAvatar(
                modifier = Modifier.size(48.dp),
                imageUrl = user.avatar,
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
                    .weight(1F),
                horizontalAlignment = Alignment.Start,
            ) {
                FreadRichText(
                    modifier = Modifier.fillMaxWidth(),
                    richText = user.humanizedName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSizeSp = 16F,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.webFinger.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                FreadRichText(
                    modifier = Modifier.fillMaxWidth(),
                    content = user.description,
                    maxLines = 6,
                )
            }
            actionButton()
        }
        if (showDivider) {
            HorizontalDivider()
        }
    }
}

@Composable
fun CommonUserPlaceHolder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        BlogAuthorAvatar(
            modifier = Modifier.size(48.dp),
            imageUrl = null,
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
                .weight(1F),
            horizontalAlignment = Alignment.Start,
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(18.dp)
                    .freadPlaceholder(true)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp)
                    .freadPlaceholder(true)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .freadPlaceholder(true)
            )
        }
    }
}
