package com.zhangke.fread.activitypub.app.internal.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.DefaultFailed
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.keyboardAsState
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_add_list_hide_in_timeline
import com.zhangke.fread.activitypub.app.activity_pub_add_list_hide_in_timeline_desc
import com.zhangke.fread.activitypub.app.activity_pub_add_list_name
import com.zhangke.fread.activitypub.app.activity_pub_add_list_remove_user_message
import com.zhangke.fread.activitypub.app.activity_pub_add_list_replies
import com.zhangke.fread.activitypub.app.activity_pub_add_list_replies_followers
import com.zhangke.fread.activitypub.app.activity_pub_add_list_replies_list
import com.zhangke.fread.activitypub.app.activity_pub_add_list_replies_non
import com.zhangke.fread.activitypub.app.activity_pub_add_list_title
import com.zhangke.fread.activitypub.app.internal.screen.list.edit.ListRepliesPolicy
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ListDetailPageContent(
    name: String,
    showSaveButton: Boolean,
    repliesPolicy: ListRepliesPolicy,
    exclusive: Boolean,
    showLoadingCover: Boolean,
    accountList: List<ActivityPubAccountEntity>,
    snackBarState: SnackbarHostState,
    accountsLoading: Boolean,
    loadAccountsError: Throwable?,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onAddUserClick: () -> Unit,
    onNameChangedRequest: (String) -> Unit,
    onExclusiveChangeRequest: (Boolean) -> Unit,
    onRemoveAccount: (ActivityPubAccountEntity) -> Unit,
    onRetryLoadAccountsClick: () -> Unit,
    onPolicySelect: (ListRepliesPolicy) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = name.ifEmpty { stringResource(Res.string.activity_pub_add_list_title) },
                onBackClick = onBackClick,
                actions = {
                    if (showSaveButton) {
                        IconButton(
                            onClick = onSaveClick,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save",
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surface,
                onClick = onAddUserClick,
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Add),
                    contentDescription = "Add User",
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    ListDetailSetting(
                        name = name,
                        repliesPolicy = repliesPolicy,
                        exclusive = exclusive,
                        onExclusiveChangeRequest = onExclusiveChangeRequest,
                        onNameChangedRequest = onNameChangedRequest,
                        onPolicySelect = onPolicySelect,
                    )
                }
                if (accountList.isNotEmpty()) {

                    items(accountList) {
                        AccountItem(
                            account = it,
                            onRemoveAccount = onRemoveAccount,
                        )
                    }
                } else if (accountsLoading) {
                    items(20) {
                        AccountPlaceholder()
                    }
                } else if (loadAccountsError != null) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(300.dp)
                        ) {
                            DefaultFailed(
                                modifier = Modifier.fillMaxSize(),
                                exception = loadAccountsError,
                                onRetryClick = onRetryLoadAccountsClick,
                            )
                        }
                    }
                }
            }
            if (showLoadingCover) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceDim)
                        .noRippleClick { }
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp))
                }
            }
        }
    }
}

@Composable
private fun ListDetailSetting(
    name: String,
    repliesPolicy: ListRepliesPolicy,
    exclusive: Boolean,
    onExclusiveChangeRequest: (Boolean) -> Unit,
    onNameChangedRequest: (String) -> Unit,
    onPolicySelect: (ListRepliesPolicy) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus()
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        var inputtedName by remember(name) { mutableStateOf(name) }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .onFocusChanged {
                    if (!it.hasFocus) {
                        onNameChangedRequest(inputtedName)
                    }
                },
            value = inputtedName,
            onValueChange = { inputtedName = it },
            label = {
                Text(
                    text = stringResource(Res.string.activity_pub_add_list_name)
                )
            },
        )
        var showPolicySelector by remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier = Modifier
                .noRippleClick { showPolicySelector = true }
                .focusable(false)
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            value = repliesPolicy.showName,
            readOnly = true,
            onValueChange = { },
            label = {
                Text(text = stringResource(Res.string.activity_pub_add_list_replies))
            },
        )
        DropdownMenu(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            expanded = showPolicySelector,
            onDismissRequest = { showPolicySelector = false },
        ) {
            ListRepliesPolicy.entries.forEach { policy ->
                DropdownMenuItem(
                    text = { Text(text = policy.showName) },
                    onClick = { onPolicySelect(policy) },
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 8.dp)
        ) {
            Column(modifier = Modifier.weight(1F)) {
                Text(
                    text = stringResource(Res.string.activity_pub_add_list_hide_in_timeline),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    modifier = Modifier.padding(top = 1.dp),
                    text = stringResource(Res.string.activity_pub_add_list_hide_in_timeline_desc),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Switch(
                modifier = Modifier.padding(start = 8.dp),
                checked = exclusive,
                onCheckedChange = {
                    onExclusiveChangeRequest(it)
                },
            )
        }

        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = stringResource(Res.string.activity_pub_add_list_replies_list),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun AccountItem(
    account: ActivityPubAccountEntity,
    onRemoveAccount: (ActivityPubAccountEntity) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BlogAuthorAvatar(
            modifier = Modifier.size(42.dp),
            imageUrl = account.avatar,
        )
        Column(
            modifier = Modifier.weight(1F).padding(start = 8.dp),
        ) {
            Text(
                text = account.displayName,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = account.acct,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        var showRemoveConfirmDialog by remember { mutableStateOf(false) }
        IconButton(
            modifier = Modifier.padding(start = 8.dp),
            onClick = { showRemoveConfirmDialog = true },
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove",
            )
        }
        if (showRemoveConfirmDialog) {
            FreadDialog(
                onDismissRequest = { showRemoveConfirmDialog = false },
                contentText = stringResource(Res.string.activity_pub_add_list_remove_user_message),
                onNegativeClick = { showRemoveConfirmDialog = false },
                onPositiveClick = {
                    showRemoveConfirmDialog = false
                    onRemoveAccount(account)
                },
            )
        }
    }
}

@Composable
private fun AccountPlaceholder() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(42.dp).clip(CircleShape).freadPlaceholder(true))

        Column(
            modifier = Modifier.weight(1F).padding(start = 8.dp),
        ) {
            Box(modifier = Modifier.height(16.dp).width(100.dp).freadPlaceholder(true))
            Box(
                modifier = Modifier.padding(top = 2.dp).height(14.dp).width(100.dp)
                    .freadPlaceholder(true)
            )
        }
    }
}

private val ListRepliesPolicy.showName: String
    @Composable get() {
        return when (this) {
            ListRepliesPolicy.FOLLOWING -> stringResource(Res.string.activity_pub_add_list_replies_followers)
            ListRepliesPolicy.LIST -> stringResource(Res.string.activity_pub_add_list_replies_list)
            ListRepliesPolicy.NONE -> stringResource(Res.string.activity_pub_add_list_replies_non)
        }
    }
