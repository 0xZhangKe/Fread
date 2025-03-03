package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.utils.HighlightTextBuildUtil
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.pxToDp
import com.zhangke.fread.bluesky.internal.model.ReplySetting
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.InputBlogTextField
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_text_hint
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_title
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_reply_input_hint
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.publish.NameAndAccountInfo
import com.zhangke.fread.status.ui.publish.PublishBlogStyleDefault
import com.zhangke.fread.status.ui.reply.BlogInReply
import com.zhangke.fread.status.ui.threads.ThreadsType
import com.zhangke.fread.status.ui.threads.blogBeReplyThreads
import com.zhangke.fread.status.ui.threads.blogInReplyingThreads
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

class PublishPostScreen(
    private val role: IdentityRole,
    private val replyToJsonString: String? = null,
    private val quoteJsonString: String? = null,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<PublishPostViewModel, PublishPostViewModel.Factory> {
            it.create(role, replyToJsonString, quoteJsonString)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        PublishPostContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onBackClick = navigator::pop,
            onContentChanged = viewModel::onContentChanged,
            onQuoteChange = viewModel::onQuoteChange,
            onSettingSelected = viewModel::onReplySettingChange,
            onSettingOptionsSelected = viewModel::onSettingOptionsSelected,
            onMediaSelected = viewModel::onMediaSelected,
            onLanguageSelected = viewModel::onLanguageSelected,
            onMediaAltChanged = viewModel::onMediaAltChanged,
            onMediaDeleteClick = viewModel::onMediaDeleteClick,
            onPublishClick = viewModel::onPublishClick,
        )
        ConsumeSnackbarFlow(snackBarHostState, viewModel.snackBarMessageFlow)
        ConsumeFlow(viewModel.finishPageFlow) {
            navigator.pop()
        }
    }

    @Composable
    private fun PublishPostContent(
        uiState: PublishPostUiState,
        snackBarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onContentChanged: (TextFieldValue) -> Unit,
        onQuoteChange: (Boolean) -> Unit,
        onSettingSelected: (ReplySetting) -> Unit,
        onSettingOptionsSelected: (ReplySetting.CombineOption) -> Unit,
        onMediaSelected: (List<PlatformUri>) -> Unit,
        onLanguageSelected: (List<String>) -> Unit,
        onMediaAltChanged: (PublishPostMediaAttachmentFile, String) -> Unit,
        onMediaDeleteClick: (PublishPostMediaAttachmentFile) -> Unit,
        onPublishClick: () -> Unit,
    ) {
        val density = LocalDensity.current
        val publishBlogStyle = PublishBlogStyleDefault.defaultStyle()
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.shared_publish_blog_title),
                    onBackClick = onBackClick,
                    actions = {
                        if (uiState.publishing) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        } else {
                            SimpleIconButton(
                                onClick = onPublishClick,
                                tint = MaterialTheme.colorScheme.primary,
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Publish",
                            )
                        }
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
            bottomBar = {
                PublishBottomPanel(
                    uiState = uiState,
                    onMediaSelected = onMediaSelected,
                    onLanguageSelected = onLanguageSelected,
                    selectedLanguages = uiState.selectedLanguages,
                    maxLanguageCount = uiState.maxLanguageCount,
                )
            },
        ) { innerPadding ->
            var contentHeight: Float? by remember { mutableStateOf(null) }
            var replyingHeight: Float? by remember { mutableStateOf(null) }
            val scrollState = rememberScrollState()
            if (uiState.replying && replyingHeight != null) {
                LaunchedEffect(replyingHeight) {
                    delay(300)
                    scrollState.animateScrollBy(replyingHeight!!)
                }
            }
            Column(
                modifier = Modifier.fillMaxSize()
                    .onGloballyPositioned {
                        if (contentHeight === null || contentHeight == 0F) {
                            contentHeight = it.size.height.toFloat()
                        }
                    }
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .let {
                        if (contentHeight != null && replyingHeight != null) {
                            it.heightIn(min = (contentHeight!! + replyingHeight!!).pxToDp(density))
                        } else {
                            it
                        }
                    },
            ) {
                if (uiState.replying) {
                    BlogInReply(
                        modifier = Modifier.fillMaxWidth()
                            .onGloballyPositioned {
                                if (replyingHeight == null || replyingHeight == 0F) {
                                    replyingHeight = it.size.height.toFloat()
                                }
                            }
                            .blogBeReplyThreads(
                                threadsType = ThreadsType.ANCESTOR,
                                publishBlogStyle = publishBlogStyle,
                            )
                            .padding(
                                start = publishBlogStyle.startPadding,
                                end = publishBlogStyle.endPadding,
                            ),
                        blog = uiState.replyBlog!!,
                        style = publishBlogStyle,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .let {
                            if (uiState.replying) {
                                it.blogInReplyingThreads(
                                    threadsType = ThreadsType.ANCHOR,
                                    publishBlogStyle = publishBlogStyle,
                                ).padding(top = publishBlogStyle.topPadding)
                            } else {
                                it
                            }
                        },
                ) {
                    AutoSizeImage(
                        url = uiState.account?.avatar.orEmpty(),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clip(CircleShape)
                            .size(publishBlogStyle.avatarSize),
                        contentDescription = null,
                    )
                    Column(
                        modifier = Modifier.weight(1F).padding(horizontal = 8.dp),
                    ) {
                        NameAndAccountInfo(
                            modifier = Modifier.fillMaxWidth(),
                            name = uiState.account?.userName.orEmpty(),
                            handle = uiState.account?.user?.prettyHandle.orEmpty(),
                            style = publishBlogStyle,
                        )
                        PostInteractionSettingLabel(
                            modifier = Modifier.padding(top = 1.dp),
                            setting = uiState.interactionSetting,
                            lists = uiState.list,
                            onQuoteChange = onQuoteChange,
                            onSettingSelected = onSettingSelected,
                            onSettingOptionsSelected = onSettingOptionsSelected,
                        )
                    }
                }
                InputBlogTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldValue = uiState.content,
                    onContentChanged = onContentChanged,
                    placeholder = if (uiState.replying) {
                        HighlightTextBuildUtil.buildHighlightText(
                            text = stringResource(
                                Res.string.shared_publish_reply_input_hint,
                                uiState.replyBlog!!.author.prettyHandle,
                            ),
                            highLightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7F)
                        )
                    } else {
                        buildAnnotatedString {
                            append(stringResource(Res.string.shared_publish_blog_text_hint))
                        }
                    },
                )

                if (uiState.attachment != null) {
                    PublishPostMediaAttachment(
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                            .fillMaxWidth(),
                        media = uiState.attachment,
                        mediaAltMaxCharacters = uiState.mediaAltMaxCharacters,
                        onAltChanged = onMediaAltChanged,
                        onDeleteClick = onMediaDeleteClick,
                    )
                }
            }
        }
    }
}
