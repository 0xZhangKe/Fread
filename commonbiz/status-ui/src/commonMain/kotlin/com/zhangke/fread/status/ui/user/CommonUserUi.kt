package com.zhangke.fread.status.ui.user

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.common.resources.logo
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun CommonAccountUi(
    modifier: Modifier,
    account: LoggedAccount,
    showDivider: Boolean,
) {
    CommonProfileUi(
        modifier = modifier,
        avatar = account.avatar.orEmpty(),
        displayName = account.humanizedName,
        handle = account.prettyHandle,
        description = account.humanizedDescription,
        showDivider = showDivider,
        protocol = account.platform.protocol,
        showProtocolLabel = true,
    )
}

@Composable
fun BasicAccountUi(
    modifier: Modifier,
    account: LoggedAccount,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BlogAuthorAvatar(
            modifier = Modifier.size(42.dp),
            imageUrl = account.avatar,
        )
        Column(
            modifier = Modifier.weight(1F)
                .padding(start = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FreadRichText(
                    modifier = Modifier,
                    richText = account.humanizedName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSizeSp = 16F,
                )
                Image(
                    modifier = Modifier.padding(start = 4.dp).size(16.dp),
                    painter = rememberVectorPainter(account.platform.protocol.logo),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = account.prettyHandle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun CommonUserUi(
    user: BlogAuthor,
    modifier: Modifier = Modifier,
    actionButton: @Composable RowScope.() -> Unit = {},
    showDivider: Boolean = true,
) {
    CommonProfileUi(
        modifier = modifier,
        avatar = user.avatar.orEmpty(),
        displayName = user.humanizedName,
        handle = user.prettyHandle,
        description = user.humanizedDescription,
        protocol = null,
        showProtocolLabel = false,
        showDivider = showDivider,
        actionButton = actionButton,
    )
}

@Composable
private fun CommonProfileUi(
    modifier: Modifier,
    avatar: String,
    displayName: RichText,
    handle: String,
    description: RichText,
    protocol: StatusProviderProtocol?,
    showProtocolLabel: Boolean,
    showDivider: Boolean,
    actionButton: @Composable RowScope.() -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            BlogAuthorAvatar(
                modifier = Modifier.size(48.dp),
                imageUrl = avatar,
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
                    .weight(1F),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FreadRichText(
                        modifier = Modifier,
                        richText = displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSizeSp = 16F,
                    )
                    if (showProtocolLabel && protocol != null) {
                        Image(
                            modifier = Modifier.padding(start = 4.dp).size(16.dp),
                            painter = rememberVectorPainter(protocol.logo),
                            contentDescription = null,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = handle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                FreadRichText(
                    modifier = Modifier.fillMaxWidth(),
                    richText = description,
                    maxLines = 6,
                )
            }
            actionButton()
        }
        if (showDivider) {
            HorizontalDivider(
                thickness = 0.5.dp,
            )
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
