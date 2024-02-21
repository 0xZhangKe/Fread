package com.zhangke.utopia.rss.internal.screen.source

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.rss.R
import com.zhangke.utopia.status.ui.BlogAuthorAvatar

@Destination(RssSourceRoute.ROUTE)
class RssSourceScreen(
    @Router private val route: String = "",
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<RssSourceViewModel, RssSourceViewModel.Factory> {
            it.create(RssSourceRoute.parseRoute(route))
        }
        val uiState by viewModel.uiState.collectAsState()
        RssSourceContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            onDisplayNameChanged = {

            },
        )
    }

    @Composable
    private fun RssSourceContent(
        uiState: RssSourceUiState,
        onBackClick: () -> Unit,
        onDisplayNameChanged: (String) -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    onBackClick = onBackClick,
                    title = stringResource(R.string.rss_source_detail_screen_title),
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                BlogAuthorAvatar(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 36.dp)
                        .size(80.dp)
                        .utopiaPlaceholder(uiState.source?.thumbnail.isNullOrEmpty()),
                    imageUrl = uiState.source?.thumbnail,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .utopiaPlaceholder(uiState.source?.title.isNullOrEmpty()),
                    style = MaterialTheme.typography.titleMedium,
                    text = uiState.source?.title.orEmpty(),
                )

                val rssSource = uiState.source
                if (rssSource != null) {
                    CustomTitleItem(
                        displayName = rssSource.displayName,
                        onDisplayNameChanged = onDisplayNameChanged,
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
            content = displayName.orEmpty(),
            option = {
                SimpleIconButton(
                    onClick = { showEditDisplayNameDialog = true },
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                )
            },
        )

    }

    @Composable
    private fun RssInfoItem(
        title: String,
        content: String,
        option: (@Composable () -> Unit)? = null,
    ) {
        Surface(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
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
}
