package com.zhangke.fread.commonbiz.shared.screen.status.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.select_account_open_status_empty
import com.zhangke.fread.commonbiz.shared.screen.select_account_open_status_search_failed
import com.zhangke.fread.commonbiz.shared.screen.select_account_open_status_search_in
import com.zhangke.fread.commonbiz.shared.screen.select_account_open_status_title
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.user.BasicAccountUi
import org.jetbrains.compose.resources.stringResource

class SelectAccountOpenStatusScreen(
    private val blogId: String,
    private val blogUrl: String,
    private val locator: PlatformLocator,
    private val protocol: StatusProviderProtocol,
) : BaseScreen() {

    companion object {

        fun create(statusUiState: StatusUiState): SelectAccountOpenStatusScreen {
            return SelectAccountOpenStatusScreen(
                blogId = statusUiState.status.intrinsicBlog.id,
                blogUrl = statusUiState.status.intrinsicBlog.url,
                locator = statusUiState.locator,
                protocol = statusUiState.status.platform.protocol,
            )
        }
    }

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel =
            getViewModel<SelectAccountOpenStatusViewModel, SelectAccountOpenStatusViewModel.Factory> {
                it.create(
                    blogId = blogId,
                    blogUrl = blogUrl,
                    locator = locator,
                    protocol = protocol,
                )
            }
        val uiState by viewModel.uiState.collectAsState()
        SelectAccountOpenStatusContent(
            uiState = uiState,
            onAccountClick = viewModel::onAccountClick,
            onCancelClick = viewModel::onCancelSearchClick,
            onSearchFailedClick = viewModel::onSearchFailedClick,
        )
        ConsumeFlow(viewModel.searchedStatusFlow) {
            bottomSheetNavigator.hide()
            GlobalScreenNavigation.navigate(StatusContextScreen.create(it))
        }
    }

    @Composable
    private fun SelectAccountOpenStatusContent(
        uiState: SelectAccountOpenStatusUiState,
        onAccountClick: (LoggedAccount) -> Unit,
        onCancelClick: () -> Unit,
        onSearchFailedClick: () -> Unit,
    ) {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .padding(bottom = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.select_account_open_status_title),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (!uiState.loadingAccounts && uiState.accountList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .padding(vertical = 38.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.select_account_open_status_empty)
                        )
                    }
                } else {
                    val pagerState = rememberPagerState { 2 }
                    LaunchedEffect(uiState.searching) {
                        val target = if (uiState.searching) 1 else 0
                        pagerState.animateScrollToPage(target)
                    }
                    HorizontalPager(
                        modifier = Modifier.fillMaxWidth(),
                        state = pagerState,
                        userScrollEnabled = false,
                    ) { page ->
                        if (page == 0) {
                            AccountsListContent(
                                uiState = uiState,
                                onAccountClick = onAccountClick,
                            )
                        } else {
                            SearchContent(
                                uiState = uiState,
                                onCancelClick = onCancelClick,
                                onSearchFailedClick = onSearchFailedClick,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountsListContent(
        uiState: SelectAccountOpenStatusUiState,
        onAccountClick: (LoggedAccount) -> Unit,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            for (account in uiState.accountList) {
                BasicAccountUi(
                    modifier = Modifier.fillMaxWidth()
                        .clickable { onAccountClick(account) },
                    account = account,
                )
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }

    @Composable
    private fun SearchContent(
        uiState: SelectAccountOpenStatusUiState,
        onCancelClick: () -> Unit,
        onSearchFailedClick: () -> Unit,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.searchFailed) {
                Text(
                    text = stringResource(Res.string.select_account_open_status_search_failed),
                    style = MaterialTheme.typography.titleMedium,
                )
            } else {
                Text(
                    text = stringResource(
                        Res.string.select_account_open_status_search_in,
                        uiState.searchingAccount?.platform?.name.orEmpty(),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = if (uiState.searchFailed) {
                    onSearchFailedClick
                } else {
                    onCancelClick
                },
            ) {
                Text(
                    text = stringResource(LocalizedString.cancel)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
