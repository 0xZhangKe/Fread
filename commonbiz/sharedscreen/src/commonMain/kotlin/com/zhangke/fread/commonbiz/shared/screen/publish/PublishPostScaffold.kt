package com.zhangke.fread.commonbiz.shared.screen.publish

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.keyboardAsState
import com.zhangke.framework.utils.HighlightTextBuildUtil
import com.zhangke.framework.utils.pxToDp
import com.zhangke.framework.utils.toPx
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.InputBlogTextField
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.embed.BlogInEmbedding
import com.zhangke.fread.status.ui.publish.NameAndAccountInfo
import com.zhangke.fread.status.ui.publish.PublishBlogStyle
import com.zhangke.fread.status.ui.publish.PublishBlogStyleDefault
import com.zhangke.fread.status.ui.threads.ThreadsType
import com.zhangke.fread.status.ui.threads.blogBeReplyThreads
import com.zhangke.fread.status.ui.threads.blogInReplyingThreads
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun PublishPostScaffold(
    account: LoggedAccount?,
    snackBarHostState: SnackbarHostState,
    content: TextFieldValue,
    showSwitchAccountIcon: Boolean,
    showAddAccountIcon: Boolean,
    publishing: Boolean,
    replyingBlog: Blog? = null,
    onContentChanged: (TextFieldValue) -> Unit,
    onPublishClick: () -> Unit,
    onBackClick: () -> Unit,
    onSwitchAccountClick: () -> Unit = {},
    onAddAccountClick: () -> Unit = {},
    contentWarning: @Composable () -> Unit = {},
    postSettingLabel: @Composable () -> Unit,
    bottomPanel: @Composable () -> Unit,
    attachment: @Composable (PublishBlogStyle) -> Unit,
) {
    val density = LocalDensity.current
    val style = PublishBlogStyleDefault.defaultStyle()
    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus()
        }
    }
    Scaffold(
        topBar = {
            PublishTopBar(
                publishing = publishing,
                onBackClick = onBackClick,
                onPublishClick = onPublishClick,
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = { bottomPanel() },
    ) { innerPadding ->
        var contentHeight: Float? by remember { mutableStateOf(null) }
        var replyingHeight: Float? by remember { mutableStateOf(null) }
        val scrollState = rememberScrollState()
        if (replyingBlog != null && replyingHeight != null) {
            LaunchedEffect(replyingHeight) {
                delay(300)
                scrollState.animateScrollBy(replyingHeight!!)
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .onSizeChanged {
                    if (contentHeight === null || contentHeight == 0F) {
                        contentHeight = it.height.toFloat()
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
            if (replyingBlog != null) {
                BlogInEmbedding(
                    modifier = Modifier.fillMaxWidth()
                        .onSizeChanged {
                            if (replyingHeight == null || replyingHeight == 0F) {
                                replyingHeight = it.height.toFloat()
                            }
                        }
                        .blogBeReplyThreads(
                            threadsType = ThreadsType.ANCESTOR,
                            publishBlogStyle = style,
                        )
                        .padding(
                            start = style.startPadding,
                            end = style.endPadding,
                        ),
                    blog = replyingBlog,
                    style = style.statusStyle,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .let {
                        if (replyingBlog != null) {
                            it.blogInReplyingThreads(
                                threadsType = ThreadsType.ANCHOR,
                                infoToTopSpacing = style.topPadding.toPx(),
                                publishBlogStyle = style,
                            ).padding(top = style.topPadding)
                        } else {
                            it
                        }
                    },
            ) {
                AutoSizeImage(
                    url = account?.avatar.orEmpty(),
                    modifier = Modifier.padding(start = 16.dp)
                        .clip(CircleShape)
                        .size(style.statusStyle.infoLineStyle.avatarSize),
                    contentDescription = null,
                )
                Column(
                    modifier = Modifier.weight(1F).padding(horizontal = 8.dp),
                ) {
                    NameAndAccountInfo(
                        modifier = Modifier.fillMaxWidth(),
                        humanizedName = RichText(account?.userName.orEmpty()),
                        handle = account?.prettyHandle.orEmpty(),
                        style = style.statusStyle,
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    postSettingLabel()
                }
                if (showSwitchAccountIcon) {
                    SimpleIconButton(
                        modifier = Modifier,
                        onClick = onSwitchAccountClick,
                        imageVector = Icons.Default.Group,
                        iconSize = 36.dp,
                        contentDescription = "Switch Account",
                    )
                }
                if (showAddAccountIcon) {
                    Spacer(modifier = Modifier.width(2.dp))
                    SimpleIconButton(
                        modifier = Modifier,
                        onClick = onAddAccountClick,
                        imageVector = Icons.Default.PersonAdd,
                        iconSize = 36.dp,
                        contentDescription = "Multi Account",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            contentWarning()
            InputBlogTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldValue = content,
                onContentChanged = onContentChanged,
                placeholder = if (replyingBlog != null) {
                    HighlightTextBuildUtil.buildHighlightText(
                        text = stringResource(
                            LocalizedString.sharedPublishReplyInputHint,
                            replyingBlog.author.prettyHandle,
                        ),
                        highLightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7F)
                    )
                } else {
                    buildAnnotatedString {
                        append(stringResource(LocalizedString.sharedPublishBlogTextHint))
                    }
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            attachment(style)
        }
    }
}
