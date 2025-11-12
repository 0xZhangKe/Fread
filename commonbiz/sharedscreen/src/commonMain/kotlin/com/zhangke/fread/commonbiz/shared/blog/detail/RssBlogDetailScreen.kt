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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.composable.WebViewPreviewer
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.ui.StatusInfoLine
import com.zhangke.fread.status.ui.style.StatusStyles
import com.zhangke.fread.status.utils.DateTimeFormatter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

class RssBlogDetailScreen(
    private val serializedBlog: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val blog: Blog = remember {
            globalJson.decodeFromString(serializedBlog)
        }
        val navigator = LocalNavigator.currentOrThrow
        val browserLauncher = LocalActivityBrowserLauncher.current

        val viewModel = getViewModel<RssBlogDetailViewModel>()
        val coroutineScope = rememberCoroutineScope()
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
        Scaffold(
            topBar = {
                Toolbar(
                    title = blog.title.ifNullOrEmpty {
                        stringResource(LocalizedString.sharedStatusContextScreenTitle)
                    },
                    onBackClick = navigator::pop,
                    actions = {
                        SimpleIconButton(
                            onClick = {
                                coroutineScope.launch {
                                    browserLauncher.launchWebTabInApp(blog.url, checkAppSupportPage = false)
                                }
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
                val displayTime by produceState("", blog.createAt) {
                    value =
                        DateTimeFormatter.format(blog.createAt.instant.toEpochMilliseconds())
                }
                StatusInfoLine(
                    modifier = Modifier.fillMaxWidth(),
                    blog = blog,
                    isOwner = false,
                    visibility = blog.visibility,
                    displayTime = displayTime,
                    style = StatusStyles.medium(),
                    onInteractive = { _, _ -> },
                    onUserInfoClick = viewModel::onUserInfoClick,
                    onUrlClick = {
                        coroutineScope.launch {
                            browserLauncher.launchWebTabInApp(it)
                        }
                    },
                    blogTranslationState = BlogTranslationUiState(support = false),
                    editedAt = blog.editedAt?.instant,
                    showOpenBlogWithOtherAccountBtn = false,
                    allowToShowFollowButton = false,
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
