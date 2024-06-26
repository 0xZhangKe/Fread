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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.browser.BrowserLauncher
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.web.WebViewPreviewer
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.utils.DateTimeFormatter
import com.zhangke.fread.commonbiz.shared.screen.R
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.ui.StatusInfoLine
import com.zhangke.fread.status.ui.style.defaultStatusStyle

class BlogDetailScreen(
    private val blog: Blog,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val viewModel = getScreenModel<BlogDetailViewModel>()
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
        Scaffold(
            topBar = {
                Toolbar(
                    title = blog.title.ifNullOrEmpty { stringResource(R.string.shared_status_context_screen_title) },
                    onBackClick = navigator::pop,
                    actions = {
                        SimpleIconButton(
                            onClick = {
                                BrowserLauncher.launchWebTabInApp(context, blog.url)
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
                StatusInfoLine(
                    modifier = Modifier.fillMaxWidth(),
                    blogAuthor = blog.author,
                    blogUrl = blog.url,
                    displayTime = DateTimeFormatter.format(context, blog.date.time),
                    style = defaultStatusStyle(),
                    moreInteractions = emptyList(),
                    onInteractive = {},
                    onUserInfoClick = viewModel::onUserInfoClick,
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
