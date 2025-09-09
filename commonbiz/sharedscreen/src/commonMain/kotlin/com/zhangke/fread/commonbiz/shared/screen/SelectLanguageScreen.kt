package com.zhangke.fread.commonbiz.shared.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(LocalizedString.sharedSelectLanguageTitle),
                    onBackClick = navigator::pop,
                    actions = {
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
                                    contentDescription = "Confirm",
                                )
                            }
                        }
                    }
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
            ) {
                items(languageList) { languageUiState ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable {
                                if (!multipleSelection) {
                                    onSelected(listOf(languageUiState.local.languageCode))
                                    navigator.pop()
                                    return@clickable
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
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .padding(start = 16.dp, end = 16.dp),
                        ) {
                            val displayName = remember {
                                languageUiState.local.getDisplayName(languageUiState.local)
                            }
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterStart),
                                text = displayName,
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
            }
        }
    }
}

data class LanguageUiState(
    val local: Locale,
    val selected: Boolean,
)
