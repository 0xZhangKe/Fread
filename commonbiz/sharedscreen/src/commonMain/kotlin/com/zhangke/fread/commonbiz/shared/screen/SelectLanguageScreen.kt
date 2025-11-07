package com.zhangke.fread.commonbiz.shared.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.utils.LanguageUtils
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.getDisplayName
import com.zhangke.framework.utils.languageCode
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource
import kotlin.jvm.Transient

class SelectLanguageScreen(
    private val selectedLanguages: List<String> = emptyList(),
    private val maxSelectCount: Int = 1,
    @Transient private val onSelected: (List<String>) -> Unit,
) : BaseScreen() {

    private val multipleSelection = maxSelectCount > 1

    @Composable
    override fun Content() {
        super.Content()
        val languageList = remember {
            val list = LanguageUtils.getAllLanguages().map { language ->
                val selected = selectedLanguages.any { language.languageCode == it }
                LanguageUiState(language, selected)
            }
            mutableStateListOf(*list.toTypedArray())
        }
        val navigator = LocalNavigator.currentOrThrow
        fun onLanguageSelected(languageUiState: LanguageUiState) {
            if (!multipleSelection) {
                onSelected(listOf(languageUiState.local.languageCode))
                navigator.pop()
                return
            }
            if (languageUiState.selected) {
                languageList[languageList.indexOf(languageUiState)] =
                    languageUiState.copy(selected = false)
            } else {
                if (languageList.count { it.selected } < maxSelectCount) {
                    languageList[languageList.indexOf(languageUiState)] =
                        languageUiState.copy(selected = true)
                }
            }
        }
        Scaffold(
            topBar = {
                var toolbarVisible by remember { mutableStateOf(true) }
                AnimatedVisibility(
                    visible = toolbarVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Toolbar(
                        title = stringResource(LocalizedString.sharedSelectLanguageTitle),
                        onBackClick = navigator::pop,
                        actions = {
                            IconButton(
                                onClick = { toolbarVisible = false },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(LocalizedString.search),
                                )
                            }
                            if (multipleSelection) {
                                IconButton(
                                    onClick = {
                                        onSelected(languageList.filter { it.selected }
                                            .map { it.local.languageCode })
                                        navigator.pop()
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = stringResource(LocalizedString.ok),
                                    )
                                }
                            }
                        }
                    )
                }
                var query by rememberSaveable { mutableStateOf("") }
                AnimatedVisibility(
                    visible = !toolbarVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    SearchLanguageBar(
                        query = query,
                        onQueryChanged = { query = it },
                        list = languageList,
                        onClose = { toolbarVisible = true },
                        onLanguageClicked = { onLanguageSelected(it) },
                    )
                }
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
            ) {
                if (languageList.count { it.selected } > 0) {
                    stickyHeader {
                        LazyRow(
                            verticalAlignment = Alignment.CenterVertically,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        ) {
                            languageList.filter { it.selected }
                                .reversed()
                                .forEach { languageUiState ->
                                    item {
                                        SelectedLanguageItem(
                                            languageUiState = languageUiState,
                                            onRemoveClick = {
                                                languageList[languageList.indexOf(languageUiState)] =
                                                    languageUiState.copy(selected = false)
                                            }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }
                                }
                        }
                    }
                }
                items(languageList) { languageUiState ->
                    LanguageItem(
                        languageUiState = languageUiState,
                        onLanguageClicked = { onLanguageSelected(it) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchLanguageBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClose: () -> Unit,
    list: List<LanguageUiState>,
    onLanguageClicked: (LanguageUiState) -> Unit,
) {
    val currentLanguageList = remember {
        mutableStateListOf(*list.filterByQuery(query).toTypedArray())
    }
    SearchBar(
        modifier = Modifier.fillMaxWidth().systemBarsPadding(),
        expanded = true,
        windowInsets = WindowInsets.statusBars,
        onExpandedChange = { onClose() },
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { q ->
                    onQueryChanged(q)
                    currentLanguageList.clear()
                    if (q.isNotEmpty()) {
                        currentLanguageList += list.filterByQuery(q)
                    }
                },
                onSearch = {},
                expanded = true,
                onExpandedChange = {
                    if (!it) {
                        onClose()
                    }
                },
                leadingIcon = {
                    Toolbar.BackButton(onBackClick = onClose)
                },
            )
        },
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().imePadding()) {
            items(currentLanguageList) { language ->
                LanguageItem(
                    languageUiState = language,
                    onLanguageClicked = {
                        onLanguageClicked(it)
                        onClose()
                    },
                )
            }
        }
    }
}

private fun List<LanguageUiState>.filterByQuery(query: String): List<LanguageUiState> {
    if (query.isEmpty()) return emptyList()
    return this.filter { languageUiState ->
        val displayName = languageUiState.local.getDisplayName(languageUiState.local).lowercase()
        val lowercaseQuery = query.lowercase()
        displayName.contains(lowercaseQuery) || lowercaseQuery.contains(displayName)
    }
}

@Composable
private fun LanguageItem(
    languageUiState: LanguageUiState,
    onLanguageClicked: (LanguageUiState) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onLanguageClicked(languageUiState) },
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                text = languageUiState.local.getDisplayName(languageUiState.local),
            )
            if (languageUiState.selected) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SelectedLanguageItem(languageUiState: LanguageUiState, onRemoveClick: () -> Unit) {
    Box(
        modifier = Modifier,
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            onClick = onRemoveClick,
            shape = RoundedCornerShape(50),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier,
                    text = languageUiState.local.getDisplayName(languageUiState.local),
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.size(2.dp))
                Icon(
                    modifier = Modifier.size(14.dp),
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Remove",
                )
            }
        }
    }
}

data class LanguageUiState(
    val local: Locale,
    val selected: Boolean,
)
