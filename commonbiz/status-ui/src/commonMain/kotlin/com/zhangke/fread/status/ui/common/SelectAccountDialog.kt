package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zhangke.framework.composable.TextWithIcon
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_switch_account_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectAccountDialog(
    accountList: List<LoggedAccount>,
    selectedAccounts: List<LoggedAccount>,
    onDismissRequest: () -> Unit,
    onAccountClicked: (LoggedAccount) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(Res.string.status_ui_switch_account_dialog_title),
                    style = MaterialTheme.typography.titleMedium
                        .copy(fontWeight = FontWeight.SemiBold),
                )
                for (account in accountList) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SelectableAccount(
                        account = account,
                        selected = selectedAccounts.any { account.uri == it.uri },
                        onClick = {
                            onDismissRequest()
                            onAccountClicked(account)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectableAccount(
    account: LoggedAccount,
    selected: Boolean,
    onClick: (LoggedAccount) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().noRippleClick { onClick(account) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BlogAuthorAvatar(
            modifier = Modifier.size(42.dp),
            imageUrl = account.avatar,
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
                .weight(1F),
        ) {
            TextWithIcon(
                text = account.userName,
                style = MaterialTheme.typography.titleMedium
                    .copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                endIcon = {
                    if (selected) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Check,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                        )
                    }
                }
            )
            Text(
                text = account.prettyHandle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
