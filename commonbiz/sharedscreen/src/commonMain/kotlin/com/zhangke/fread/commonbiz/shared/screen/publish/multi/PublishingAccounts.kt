package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.utils.getDisplayName
import com.zhangke.framework.utils.initLocale
import com.zhangke.framework.utils.languageCode
import com.zhangke.fread.common.resources.logo
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishSettingLabel
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.label
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.labelIcon
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.isActivityPub
import com.zhangke.fread.status.model.isBluesky
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.common.RemainingTextStatus
import com.zhangke.fread.status.ui.common.SelectAccountDialog
import com.zhangke.fread.status.ui.richtext.FreadRichText
import org.jetbrains.compose.resources.stringResource

@Composable
fun PublishingAccounts(
    modifier: Modifier,
    uiState: MultiAccountPublishingUiState,
    onRemoveAccountClick: (LoggedAccount) -> Unit,
    onAddAccountClick: (MultiPublishingAccountWithRules) -> Unit,
) {
    var showSelectAccountPopup by remember { mutableStateOf(false) }
    Box(
        modifier = modifier,
    ) {
        SimpleIconButton(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
            iconModifier = Modifier.border(
                width = 1.dp,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiary,
            ),
            imageVector = Icons.Rounded.Add,
            tint = MaterialTheme.colorScheme.tertiary,
            onClick = { showSelectAccountPopup = true },
            contentDescription = "Add Account",
        )
        Column(
            modifier = Modifier.padding(start = 48.dp),
        ) {
            for (index in uiState.addedAccounts.indices) {
                val account = uiState.addedAccounts[index]
                Box(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
                ) {
                    AccountItem(
                        modifier = Modifier.fillMaxWidth(),
                        uiState = uiState,
                        accountUiState = account,
                        onRemoveAccountClick = onRemoveAccountClick,
                    )
                }
                if (index < uiState.addedAccounts.lastIndex) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
    if (showSelectAccountPopup) {
        SelectAccountDialog(
            accountList = uiState.allAccounts.map { it.account },
            selectedAccounts = uiState.addedAccounts.map { it.account },
            onDismissRequest = { showSelectAccountPopup = false },
            onAccountClicked = { account ->
                showSelectAccountPopup = false
                onAddAccountClick(uiState.allAccounts.first { it.account.uri == account.uri })
            },
        )
    }
}

@Composable
private fun AccountItem(
    modifier: Modifier,
    uiState: MultiAccountPublishingUiState,
    accountUiState: MultiPublishingAccountUiState,
    onRemoveAccountClick: (LoggedAccount) -> Unit,
) {
    val account = accountUiState.account
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
            ) {
                Box(
                    modifier = Modifier.size(42.dp).align(Alignment.CenterVertically),
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
                Column(
                    modifier = Modifier.weight(1F)
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                ) {
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
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = { onRemoveAccountClick(account) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (account.platform.protocol.isBluesky) {
                    PublishSettingLabel(
                        modifier = Modifier,
                        label = uiState.interactionSetting.label,
                        icon = uiState.interactionSetting.labelIcon,
                    )
                } else if (account.platform.protocol.isActivityPub) {
                    PublishSettingLabel(
                        modifier = Modifier,
                        label = stringResource(uiState.postVisibility.describeStringId),
                        icon = Icons.Default.Public,
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                val lan = uiState.selectedLanguage
                Text(
                    text = remember(lan) { initLocale(lan.languageCode).getDisplayName(lan) },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                RemainingTextStatus(
                    modifier = Modifier.padding(start = 8.dp),
                    maxCount = accountUiState.rules.maxCharacters,
                    contentLength = uiState.content.text.length,
                )
            }
        }
    }
}
