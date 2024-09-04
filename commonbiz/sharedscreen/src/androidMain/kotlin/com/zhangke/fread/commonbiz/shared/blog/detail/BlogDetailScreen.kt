package com.zhangke.fread.commonbiz.shared.blog.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.common.utils.DateTimeFormatter
import com.zhangke.fread.commonbiz.shared.composable.WebViewPreviewer
import com.zhangke.fread.commonbiz.shared.screen.R
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.ui.StatusInfoLine
import com.zhangke.fread.status.ui.style.StatusStyles

class BlogDetailScreen(
    private val blog: Blog,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val viewModel = getViewModel<BlogDetailViewModel>()
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
        Scaffold(
            topBar = {
                Toolbar(
                    title = blog.title.ifNullOrEmpty { stringResource(R.string.shared_status_context_screen_title) },
                    onBackClick = navigator::pop,
                    actions = {
                        SimpleIconButton(
                            onClick = {
                                BrowserLauncher.launchWebTabInApp(
                                    context,
                                    blog.url,
                                    checkAppSupportPage = false
                                )
                            },
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open In Browser",
                        )
                    }
                )
            }
        ) { innerPaddings ->
            Column(
                modifier = Modifier
                    .padding(innerPaddings)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                val displayTime by produceState("", blog.date) {
                    value = DateTimeFormatter.format(blog.date.toEpochMilliseconds())
                }
                StatusInfoLine(
                    modifier = Modifier.fillMaxWidth(),
                    blogAuthor = blog.author,
                    blogUrl = blog.url,
                    visibility = blog.visibility,
                    displayTime = displayTime,
                    style = StatusStyles.medium(),
                    moreInteractions = emptyList(),
                    onInteractive = {},
                    onUserInfoClick = viewModel::onUserInfoClick,
                    onUrlClick = {
                        BrowserLauncher.launchWebTabInApp(context, it)
                    },
                    blogTranslationState = BlogTranslationUiState(support = false),
                    editedAt = blog.editedAt,
                    showFollowButton = false,
                    onTranslateClick = {},
                )
                WebViewPreviewer(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    html = blog.content,
                )
            }
        }
    }
}
