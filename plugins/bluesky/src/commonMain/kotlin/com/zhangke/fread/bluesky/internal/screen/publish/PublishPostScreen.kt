package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.TwoTextsInRow
import com.zhangke.fread.bluesky.internal.model.PostInteractionSetting
import com.zhangke.fread.bluesky.internal.model.ReplySetting
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_title
import org.jetbrains.compose.resources.stringResource

class PublishPostScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

    }

    @Composable
    private fun PublishPostContent(
        uiState: PublishPostUiState,
        snackbarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.shared_publish_blog_title),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AutoSizeImage(
                        url = uiState.account?.avatar.orEmpty(),
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(start = 16.dp)
                            .size(42.dp),
                        contentDescription = null,
                    )

                    Column(
                        modifier = Modifier.weight(1F).padding(horizontal = 16.dp),
                    ) {
                        NameAndAccountInfo(
                            modifier = Modifier.fillMaxWidth(),
                            name = uiState.account?.userName.orEmpty(),
                            handle = uiState.account?.handle.orEmpty(),
                        )

                    }
                }
            }
        }
    }


    @Composable
    private fun NameAndAccountInfo(
        modifier: Modifier,
        name: String,
        handle: String,
    ) {
        TwoTextsInRow(
            firstText = {
                Text(
                    modifier = Modifier,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                )
            },
            secondText = {
                Text(
                    modifier = Modifier,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = handle,
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            spacing = 2.dp,
            modifier = modifier,
        )
    }

    @Composable
    private fun PostInteractionSettingLabel(
        modifier: Modifier,
        setting: PostInteractionSetting,
    ) {

    }

    private val PostInteractionSetting.label: String
        @Composable get() {
            return if (this.replySetting is ReplySetting.Everybody) {
                Icons.Default.Public
            } else {
                Icons.Outlined.Group
            }
        }

    private val PostInteractionSetting.labelIcon: ImageVector
        @Composable get() {
            return if (this.replySetting is ReplySetting.Everybody) {
                Icons.Default.Public
            } else {
                Icons.Outlined.Group
            }
        }
}
