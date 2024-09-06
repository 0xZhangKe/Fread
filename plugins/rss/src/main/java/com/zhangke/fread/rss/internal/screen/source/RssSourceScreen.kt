package com.zhangke.fread.rss.internal.screen.source

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.browser.LocalBrowserLauncher
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.framework.Res
import com.zhangke.fread.framework.alert
import com.zhangke.fread.framework.cancel
import com.zhangke.fread.framework.ok
import com.zhangke.fread.rss.R
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import kotlinx.coroutines.flow.Flow

@Destination(RssSourceScreenRoute.ROUTE)
class RssSourceScreen(
    @Router private val route: String = "",
) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<RssSourceViewModel, RssSourceViewModel.Factory> {
            it.create(RssSourceScreenRoute.parseRoute(route))
        }
        val uiState by viewModel.uiState.collectAsState()
        RssSourceContent(
            uiState = uiState,
            snackBarMessageFlow = viewModel.snackBarMessageFlow,
            onBackClick = navigator::pop,
            onDisplayNameChanged = viewModel::onDisplayNameChanged,
        )
    }

    @Composable
    private fun RssSourceContent(
        uiState: RssSourceUiState,
        snackBarMessageFlow: Flow<TextString>,
        onBackClick: () -> Unit,
        onDisplayNameChanged: (String) -> Unit,
    ) {
        val context = LocalContext.current
        val browserLauncher = LocalBrowserLauncher.current
        val snackBarState = rememberSnackbarHostState()
        ConsumeSnackbarFlow(snackBarState, snackBarMessageFlow)
        Scaffold(
            topBar = {
                Toolbar(
                    onBackClick = onBackClick,
                    title = stringResource(R.string.rss_source_detail_screen_title),
                )
            },
            snackbarHost = {
                SnackbarHost(snackBarState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                BlogAuthorAvatar(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 20.dp)
                        .size(80.dp),
                    imageUrl = uiState.source?.thumbnail,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .freadPlaceholder(uiState.source?.title.isNullOrEmpty()),
                    style = MaterialTheme.typography.titleMedium,
                    text = uiState.source?.title.orEmpty(),
                )

                FreadRichText(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .freadPlaceholder(uiState.source?.description.isNullOrEmpty()),
                    content = uiState.source?.description.orEmpty(),
                    mentions = emptyList(),
                    emojis = emptyList(),
                    tags = emptyList(),
                    onHashtagClick = {},
                    onMentionClick = {},
                    onUrlClick = {
                        browserLauncher.launchWebTabInApp(it)
                    },
                )

                Spacer(modifier = Modifier.height(22.dp))

                val rssSource = uiState.source
                if (rssSource != null) {
                    CustomTitleItem(
                        displayName = rssSource.displayName,
                        onDisplayNameChanged = onDisplayNameChanged,
                    )
                    RssInfoItem(
                        modifier = Modifier.clickable {
                            browserLauncher.launchWebTabInApp(rssSource.url)
                        },
                        title = stringResource(R.string.rss_source_detail_screen_url),
                        content = rssSource.url,
                    )
                    if (rssSource.homePage.isNullOrEmpty().not()) {
                        RssInfoItem(
                            modifier = Modifier.clickable {
                                browserLauncher.launchWebTabInApp(rssSource.homePage!!)
                            },
                            title = stringResource(R.string.rss_source_detail_screen_home_url),
                            content = rssSource.homePage!!,
                        )
                    }
                    RssInfoItem(
                        title = stringResource(R.string.rss_source_detail_screen_add_date),
                        content = uiState.formattedAddDate.orEmpty(),
                    )
                    RssInfoItem(
                        title = stringResource(R.string.rss_source_detail_screen_last_update_date),
                        content = uiState.formattedLastUpdateDate.orEmpty(),
                    )
                }
            }
        }
    }

    @Composable
    private fun CustomTitleItem(
        displayName: String,
        onDisplayNameChanged: (String) -> Unit,
    ) {
        var showEditDisplayNameDialog by remember {
            mutableStateOf(false)
        }
        RssInfoItem(
            title = stringResource(R.string.rss_source_detail_screen_custom_title),
            content = displayName,
            option = {
                SimpleIconButton(
                    onClick = { showEditDisplayNameDialog = true },
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                )
            },
        )

        if (showEditDisplayNameDialog) {
            var newDisplayName by remember {
                mutableStateOf(displayName)
            }
            FreadDialog(
                title = org.jetbrains.compose.resources.stringResource(Res.string.alert),
                onDismissRequest = {
                    showEditDisplayNameDialog = false
                },
                positiveButtonText = org.jetbrains.compose.resources.stringResource(Res.string.ok),
                onPositiveClick = {
                    showEditDisplayNameDialog = false
                    if (newDisplayName != displayName) {
                        onDisplayNameChanged(newDisplayName)
                    }
                },
                negativeButtonText = org.jetbrains.compose.resources.stringResource(Res.string.cancel),
                onNegativeClick = {
                    showEditDisplayNameDialog = false
                },
                content = {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        value = newDisplayName,
                        onValueChange = {
                            newDisplayName = it
                        },
                        label = {
                            Text(text = stringResource(R.string.rss_source_detail_screen_custom_title))
                        }
                    )
                }
            )
        }
    }

    @Composable
    private fun RssInfoItem(
        title: String,
        content: String,
        modifier: Modifier = Modifier,
        option: (@Composable () -> Unit)? = null,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (option != null) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    option()
                }
            }
        }
    }
}
