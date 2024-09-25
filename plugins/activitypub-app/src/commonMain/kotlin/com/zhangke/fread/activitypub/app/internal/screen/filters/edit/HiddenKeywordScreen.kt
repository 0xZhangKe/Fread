package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_keyword_dialog_hint
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_keyword_dialog_title
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_keyword_remove_keyword_dialog_content
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_keyword_title
import com.zhangke.fread.common.page.BaseScreen
import org.jetbrains.compose.resources.stringResource

class HiddenKeywordScreen(
    private val addedKeywords: List<EditFilterUiState.Keyword>,
) : BaseScreen() {

    companion object {

        const val SCREEN_KEY =
            "com.zhangke.fread.activitypub.app.internal.screen.filters.edit.HiddenKeywordScreen"
    }

    override val key: ScreenKey
        get() = SCREEN_KEY

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val resultNavigator = LocalNavigator.currentOrThrow.navigationResult
        val keywordsList = remember(addedKeywords) {
            mutableStateListOf<EditFilterUiState.Keyword>().also { it.addAll(addedKeywords) }
        }
        val showingKeywordList = keywordsList.filter { !it.deleted }
        var pendingEditKeyword: EditFilterUiState.Keyword? by remember {
            mutableStateOf(null)
        }
        var showEditDialog by remember {
            mutableStateOf(false)
        }
        BackHandler(true) {
            resultNavigator.popWithResult(keywordsList)
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.activity_pub_filter_edit_keyword_title),
                    onBackClick = {
                        resultNavigator.popWithResult(keywordsList)
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
                    Box(
                        modifier = Modifier
                            .clickable {
                                pendingEditKeyword = keyword
                                showEditDialog = true
                            }
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterStart),
                            text = keyword.keyword,
                        )

                        var showDeleteConfirmDialog by remember {
                            mutableStateOf(false)
                        }
                        SimpleIconButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = {
                                showDeleteConfirmDialog = true
                            },
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                        )
                        if (showDeleteConfirmDialog) {
                            FreadDialog(
                                contentText = stringResource(Res.string.activity_pub_filter_edit_keyword_remove_keyword_dialog_content),
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
                onDismissRequest = {
                    showEditDialog = false
                },
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
            title = stringResource(Res.string.activity_pub_filter_edit_keyword_dialog_title),
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
                        Text(text = stringResource(Res.string.activity_pub_filter_edit_keyword_dialog_hint))
                    },
                    placeholder = {
                        Text(text = stringResource(Res.string.activity_pub_filter_edit_keyword_dialog_hint))
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
}
