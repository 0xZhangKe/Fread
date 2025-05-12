package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.resources.logo
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun PublishingAccounts(
    modifier: Modifier,
    settingBlock: @Composable () -> Unit,
    language: String,
    currentContentLength: Int,
    accounts: List<LoggedAccount>,
    onRemoveAccountClick: (LoggedAccount) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        for (account in accounts) {
            AccountItem(
                modifier = Modifier.fillMaxWidth(),
                settingBlock = settingBlock,
                language = language,
                currentContentLength = currentContentLength,
                account = account,
                onRemoveAccountClick = onRemoveAccountClick,
            )
        }
    }
}

@Composable
private fun AccountItem(
    modifier: Modifier,
    settingBlock: @Composable () -> Unit,
    language: String,
    currentContentLength: Int,
    account: LoggedAccount,
    onRemoveAccountClick: (LoggedAccount) -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(42.dp),
                ) {
                    BlogAuthorAvatar(
                        modifier = Modifier.fillMaxSize(),
                        imageUrl = account.avatar,
                    )
                    Image(
                        imageVector = account.platform.protocol.logo,
                        modifier = Modifier.align(Alignment.BottomEnd).size(16.dp),
                        contentDescription = null,
                    )
                }
                Column(modifier = Modifier.weight(1F).padding(start = 8.dp)) {
                    FreadRichText(
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        content = account.userName,
                        emojis = account.emojis,
                        onUrlClick = {},
                        fontWeight = FontWeight.SemiBold,
                        fontSizeSp = 16F,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        text = account.prettyHandle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                settingBlock()
                Spacer(modifier = Modifier.weight(1F))

            }
        }
    }
}
