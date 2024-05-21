package com.zhangke.utopia.feeds.pages.manager.import

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.utopia.feeds.R

class ImportFeedsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<ImportFeedsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        ImportFeedsContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            onImportClick = {

            },
        )
    }

    @Composable
    private fun ImportFeedsContent(
        uiState: ImportFeedsUiState,
        onBackClick: () -> Unit,
        onImportClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.feeds_import_page_title),
                    onBackClick = onBackClick,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val selectedFileLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
                        Log.d("U_TEST", "on result: $uri")
                    }
                    Surface(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .weight(1F)
                            .clickable {
                                selectedFileLauncher.launch(
                                    arrayOf(
                                        "*/*"
//                                        "text/*"
//                                        "application/*",
//                                        "application/xml",
//                                        "document/*",
//                                        "document/xml",
                                    )
                                )
                            },
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = stringResource(R.string.feeds_import_page_hint),
                                overflow = TextOverflow.Clip,
                                maxLines = 1,
                            )
                        }
                    }
                    Button(
                        modifier = Modifier.padding(end = 16.dp),
                        onClick = onImportClick,
                    ) {
                        Text(
                            text = stringResource(R.string.feeds_import_button)
                        )
                    }
                }
            }
        }
    }
}
