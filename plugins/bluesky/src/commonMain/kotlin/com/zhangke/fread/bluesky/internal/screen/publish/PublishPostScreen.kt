package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.TwoTextsInRow
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.bluesky.internal.model.PostInteractionSetting
import com.zhangke.fread.bluesky.internal.model.ReplySetting
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_title
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_follower
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_following
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_mentioned
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_quote_allow
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_quote_title
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_reply_all
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_reply_combine_title
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_reply_nobody
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_reply_subtitle
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_reply_title
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_subtitle
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_dialog_title
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_limited
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_interaction_no_limit
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PostInteractionSettingLabel(
        modifier: Modifier,
        setting: PostInteractionSetting,
        onQuoteChange: (Boolean) -> Unit,
    ) {
        var showSelector by remember { mutableStateOf(false) }
        Row(
            modifier = modifier
                .noRippleClick { showSelector = true }
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(2.dp),
                ).padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = setting.labelIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = setting.label,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        if (showSelector) {
            ModalBottomSheet(
                onDismissRequest = { showSelector = false },
                sheetState = state,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = stringResource(Res.string.shared_publish_interaction_dialog_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Text(
                        text = stringResource(Res.string.shared_publish_interaction_dialog_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
                    Text(
                        modifier = Modifier.padding(top = 26.dp),
                        text = stringResource(Res.string.shared_publish_interaction_dialog_quote_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(Res.string.shared_publish_interaction_dialog_quote_allow),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.weight(1F))
                        Switch(
                            checked = setting.allowQuote,
                            onCheckedChange = onQuoteChange,
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
                    Text(
                        modifier = Modifier.padding(top = 26.dp),
                        text = stringResource(Res.string.shared_publish_interaction_dialog_reply_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Text(
                        modifier = Modifier.padding(top = 26.dp),
                        text = stringResource(Res.string.shared_publish_interaction_dialog_reply_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        InteractionOption(
                            modifier = Modifier.weight(1F),
                            text = stringResource(Res.string.shared_publish_interaction_dialog_reply_all),
                            selected = setting.replySetting is ReplySetting.Everybody,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        InteractionOption(
                            modifier = Modifier.weight(1F),
                            text = stringResource(Res.string.shared_publish_interaction_dialog_reply_nobody),
                            selected = setting.replySetting is ReplySetting.Nobody,
                        )
                    }
                    if (setting.replySetting !is ReplySetting.Nobody) {
                        Text(
                            modifier = Modifier.padding(top = 26.dp),
                            text = stringResource(Res.string.shared_publish_interaction_dialog_reply_combine_title),
                            style = MaterialTheme.typography.bodySmall,
                        )

                        InteractionOption(
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                            text = stringResource(Res.string.shared_publish_interaction_dialog_mentioned),
                            selected = setting.replySetting.combinedMentions,
                        )

                        InteractionOption(
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                            text = stringResource(Res.string.shared_publish_interaction_dialog_following),
                            selected = setting.replySetting.combinedFollowing,
                        )

                        InteractionOption(
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                            text = stringResource(Res.string.shared_publish_interaction_dialog_follower),
                            selected = setting.replySetting.combinedFollowers,
                        )


                    }
                }
            }
        }
    }

    @Composable
    private fun InteractionOption(
        modifier: Modifier,
        text: String,
        selected: Boolean,
    ) {
        val backgroundColor = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3F)
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        }
        Row(
            modifier = modifier.background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp),
            ).padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val fontStyle = if (selected) {
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                )
            } else {
                MaterialTheme.typography.bodyMedium
            }
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = text,
                style = fontStyle,
            )
            if (selected) {
                Spacer(modifier = Modifier.weight(1F))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp),
                )
            }
        }
    }

    private val PostInteractionSetting.label: String
        @Composable get() {
            return if (this.replySetting is ReplySetting.Everybody) {
                stringResource(Res.string.shared_publish_interaction_no_limit)
            } else {
                stringResource(Res.string.shared_publish_interaction_limited)
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
