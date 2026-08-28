@file:OptIn(ExperimentalTime::class)

package com.zhangke.fread.commonbiz.shared.blog.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.icon.UnTranslate
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.translate.PostTranslationStatus
import com.zhangke.fread.common.translate.rememberPostTranslationState
import com.zhangke.fread.commonbiz.shared.composable.WebViewPreviewer
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.ui.StatusInfoLine
import com.zhangke.fread.status.ui.style.StatusStyles
import com.zhangke.fread.status.utils.DateTimeFormatter
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

@Serializable
data class RssBlogDetailScreenNavKey(val serializedBlog: String) : NavKey

@Composable
fun RssBlogDetailScreen(
    serializedBlog: String,
    viewModel: RssBlogDetailViewModel,
) {
    val snackbarHost = rememberSnackbarHostState()
    val blog: Blog = remember { globalJson.decodeFromString(serializedBlog) }
    val navigator = LocalNavBackStack.currentOrThrow
    val browserLauncher = LocalActivityBrowserLauncher.current
    val coroutineScope = rememberCoroutineScope()
    ConsumeOpenScreenFlow(viewModel.openScreenFlow)
    val translateState = rememberPostTranslationState(blog)
    val errorMessage = (translateState.status as? PostTranslationStatus.Failed)
        ?.error
        ?.message
        ?.takeIf { it.isNotEmpty() }
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHost.showSnackbar(errorMessage)
        }
    }
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.sharedStatusContextScreenTitle),
                onBackClick = navigator::removeLastOrNull,
                actions = {
                    SimpleIconButton(
                        onClick = {
                            coroutineScope.launch {
                                browserLauncher.launchWebTabInApp(
                                    blog.url,
                                    checkAppSupportPage = false
                                )
                            }
                        },
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = stringResource(LocalizedString.statusUiInteractionOpenInBrowser),
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (translateState.showTranslation) {
                                    translateState.hideTranslation()
                                } else {
                                    translateState.translatePost(blog, true)
                                }
                            }
                        },
                        enabled = translateState.status !is PostTranslationStatus.Translating,
                    ) {
                        if (translateState.status is PostTranslationStatus.Translating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Icon(
                                imageVector = if (translateState.showTranslation) {
                                    Icons.Filled.UnTranslate
                                } else {
                                    Icons.Default.Translate
                                },
                                contentDescription = stringResource(LocalizedString.statusUiInteractionTranslate),
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHost)
        },
    ) { innerPaddings ->
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(innerPaddings)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                val displayTime by produceState("", blog.createAt) {
                    value = DateTimeFormatter.format(blog.createAt.instant.toEpochMilliseconds())
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
                    editedAt = blog.editedAt?.instant,
                    showOpenBlogWithOtherAccountBtn = false,
                    allowToShowFollowButton = false,
                    onTranslateClick = {
                        coroutineScope.launch { translateState.translatePost(blog, true) }
                    },
                )
                Spacer(modifier = Modifier.fillMaxWidth().height(8.dp))
                if (!blog.title.isNullOrEmpty()) {
                    val title = if (translateState.showTranslation) {
                        (translateState.status as? PostTranslationStatus.Translated)?.translatedContent
                            ?.title
                            ?: blog.title
                    } else {
                        blog.title
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        text = title.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                val htmlContent = if (translateState.showTranslation) {
                    (translateState.status as? PostTranslationStatus.Translated)?.translatedContent
                        ?.htmlContent
                        ?: blog.content
                } else {
                    blog.content
                }
                WebViewPreviewer(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 6.dp, end = 16.dp)
                        .fillMaxSize(),
                    html = htmlContent,
                )
            }
        }
    }
}
