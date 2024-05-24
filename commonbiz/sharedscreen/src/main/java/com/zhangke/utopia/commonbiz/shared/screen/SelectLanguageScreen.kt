package com.zhangke.utopia.commonbiz.shared.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.utils.LanguageUtil
import java.util.Locale

class SelectLanguageScreen(
    private val onSelected: (Locale) -> Unit,
) : Screen {

    @Composable
    override fun Content() {
        val languageList = remember {
            LanguageUtil().getAllLanguages()
        }
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.shared_select_language_title),
                    onBackClick = navigator::pop,
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                items(languageList) { language: Locale ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable {
                                onSelected(language)
                                navigator.pop()
                            }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            val displayName = remember {
                                language.getDisplayName(language)
                            }
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 16.dp),
                                text = displayName,
                            )
                        }
                    }
                }
            }
        }
    }
}
