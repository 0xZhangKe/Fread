package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.BackHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.ScreenEventFlow
import com.zhangke.fread.localization.LocalizedString
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class HiddenKeywordScreenNavKey(
    val addedKeywords: List<EditFilterUiState.Keyword>,
) : NavKey {

    companion object {
        val keywordsListFlow = ScreenEventFlow<List<EditFilterUiState.Keyword>>()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HiddenKeywordScreen(addedKeywords: List<EditFilterUiState.Keyword>) {
    val backStack = LocalNavBackStack.currentOrThrow
    val keywordsList = remember(addedKeywords) {
        mutableStateListOf<EditFilterUiState.Keyword>().also { it.addAll(addedKeywords) }
    }
    val showingKeywordList = keywordsList.filter { !it.deleted }
    var pendingEditKeyword: EditFilterUiState.Keyword? by remember {
        mutableStateOf(null)
    }
    var showEditDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    BackHandler(true) {
        coroutineScope.launch {
            HiddenKeywordScreenNavKey.keywordsListFlow.emit(keywordsList)
            backStack.removeLastOrNull()
        }
    }
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.activity_pub_filter_edit_keyword_title),
                onBackClick = {
                    coroutineScope.launch {
                        HiddenKeywordScreenNavKey.keywordsListFlow.emit(keywordsList)
                        backStack.removeLastOrNull()
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surface,
                onClick = {
                    pendingEditKeyword = null
                    showEditDialog = true
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            items(showingKeywordList) { keyword ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = keyword.keyword,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = keyword.wholeWord,
                                onCheckedChange = {
                                    keywordsList[keywordsList.indexOf(keyword)] =
                                        keyword.copy(wholeWord = it)
                                },
                            )
                            Text(
                                text = stringResource(LocalizedString.activity_pub_filter_edit_whole_word),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    var showDeleteConfirmDialog by remember {
                        mutableStateOf(false)
                    }
                    SimpleIconButton(
                        modifier = Modifier,
                        onClick = {
                            showDeleteConfirmDialog = true
                        },
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                    )
                    if (showDeleteConfirmDialog) {
                        FreadDialog(
                            contentText = stringResource(LocalizedString.activity_pub_filter_edit_keyword_remove_keyword_dialog_content),
                            onDismissRequest = {
                                showDeleteConfirmDialog = false
                            },
                            onNegativeClick = {
                                showDeleteConfirmDialog = false
                            },
                            onPositiveClick = {
                                showDeleteConfirmDialog = false
                                keywordsList[keywordsList.indexOf(keyword)] =
                                    keyword.copy(deleted = true)
                            },
                        )
                    }
                }
            }
        }
    }
    if (showEditDialog) {
        EditKeywordDialog(
            keyword = pendingEditKeyword,
            onConfirmClick = {
                if (pendingEditKeyword == null) {
                    keywordsList.add(EditFilterUiState.Keyword(keyword = it))
                } else {
                    if (keywordsList.contains(pendingEditKeyword)) {
                        keywordsList[keywordsList.indexOf(pendingEditKeyword)] =
                            pendingEditKeyword!!.copy(keyword = it)
                    }
                }
                pendingEditKeyword = null
            },
            onDismissRequest = { showEditDialog = false },
        )
    }
}

@Composable
private fun EditKeywordDialog(
    keyword: EditFilterUiState.Keyword?,
    onConfirmClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var inputtingKeyword by remember(keyword) {
        mutableStateOf(keyword?.keyword.orEmpty())
    }
    FreadDialog(
        title = stringResource(LocalizedString.activity_pub_filter_edit_keyword_dialog_title),
        onDismissRequest = onDismissRequest,
        content = {
            OutlinedTextField(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                value = inputtingKeyword,
                onValueChange = {
                    inputtingKeyword = it
                },
                maxLines = 1,
                label = {
                    Text(text = stringResource(LocalizedString.activity_pub_filter_edit_keyword_dialog_hint))
                },
                placeholder = {
                    Text(text = stringResource(LocalizedString.activity_pub_filter_edit_keyword_dialog_hint))
                },
            )
        },
        onNegativeClick = onDismissRequest,
        onPositiveClick = {
            onDismissRequest()
            if (inputtingKeyword.isNotEmpty()) {
                onConfirmClick(inputtingKeyword)
            }
        },
    )
}
