package com.zhangke.fread.commonbiz.shared.screen.publish.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PostInteractionSetting
import com.zhangke.fread.status.model.ReplySetting
import com.zhangke.fread.status.model.StatusList
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishSettingLabel
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostInteractionSettingLabel(
    modifier: Modifier,
    setting: PostInteractionSetting,
    lists: List<StatusList>,
    onSettingSelected: (PostInteractionSetting) -> Unit,
) {
    PostInteractionSettingLabel(
        modifier = modifier,
        setting = setting,
        lists = lists,
        onQuoteChange = { onSettingSelected(setting.copy(allowQuote = it)) },
        onSettingSelected = {
            onSettingSelected(setting.copy(replySetting = it))
        },
        onSettingOptionsSelected = { option ->
            val options = setting.replySetting.let { it as? ReplySetting.Combined }
                ?.options?.toMutableList() ?: mutableListOf()
            if (option in options) {
                options.remove(option)
            } else {
                options.add(option)
            }
            onSettingSelected(setting.copy(replySetting = ReplySetting.Combined(options)))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostInteractionSettingLabel(
    modifier: Modifier,
    setting: PostInteractionSetting,
    lists: List<StatusList>,
    onQuoteChange: (Boolean) -> Unit,
    onSettingSelected: (ReplySetting) -> Unit,
    onSettingOptionsSelected: (ReplySetting.CombineOption) -> Unit,
) {
    var showSelector by remember { mutableStateOf(false) }
    PublishSettingLabel(
        modifier = modifier.noRippleClick { showSelector = true },
        label = setting.label,
        icon = setting.labelIcon,
    )
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
                    text = stringResource(LocalizedString.sharedPublishInteractionDialogTitle),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text(
                    text = stringResource(LocalizedString.sharedPublishInteractionDialogSubtitle),
                    style = MaterialTheme.typography.bodyMedium,
                )
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
                Text(
                    modifier = Modifier.padding(top = 26.dp),
                    text = stringResource(LocalizedString.sharedPublishInteractionDialogQuoteTitle),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(LocalizedString.sharedPublishInteractionDialogQuoteAllow),
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
                    text = stringResource(LocalizedString.sharedPublishInteractionDialogReplyTitle),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(LocalizedString.sharedPublishInteractionDialogReplySubtitle),
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    InteractionOption(
                        modifier = Modifier.weight(1F)
                            .noRippleClick { onSettingSelected(ReplySetting.Everybody) },
                        text = stringResource(LocalizedString.sharedPublishInteractionDialogReplyAll),
                        selected = setting.replySetting is ReplySetting.Everybody,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    InteractionOption(
                        modifier = Modifier.weight(1F)
                            .noRippleClick { onSettingSelected(ReplySetting.Nobody) },
                        text = stringResource(LocalizedString.sharedPublishInteractionDialogReplyNobody),
                        selected = setting.replySetting is ReplySetting.Nobody,
                    )
                }
                if (setting.replySetting !is ReplySetting.Nobody) {
                    Text(
                        modifier = Modifier.padding(top = 26.dp),
                        text = stringResource(LocalizedString.sharedPublishInteractionDialogReplyCombineTitle),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    InteractionOption(
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                            .noRippleClick { onSettingOptionsSelected(ReplySetting.CombineOption.Mentioned) },
                        text = stringResource(LocalizedString.sharedPublishInteractionDialogMentioned),
                        selected = setting.replySetting.combinedMentions,
                    )

                    InteractionOption(
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                            .noRippleClick { onSettingOptionsSelected(ReplySetting.CombineOption.Following) },
                        text = stringResource(LocalizedString.sharedPublishInteractionDialogFollowing),
                        selected = setting.replySetting.combinedFollowing,
                    )

                    InteractionOption(
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                            .noRippleClick { onSettingOptionsSelected(ReplySetting.CombineOption.Followers) },
                        text = stringResource(LocalizedString.sharedPublishInteractionDialogFollower),
                        selected = setting.replySetting.combinedFollowers,
                    )

                    for (listView in lists) {
                        val selected = if (setting.replySetting is ReplySetting.Combined) {
                            (setting.replySetting as ReplySetting.Combined).options
                                .filterIsInstance<ReplySetting.CombineOption.UserInList>()
                                .any { it.listView.cid == listView.cid }
                        } else {
                            false
                        }
                        InteractionOption(
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                                .noRippleClick {
                                    onSettingOptionsSelected(
                                        ReplySetting.CombineOption.UserInList(listView)
                                    )
                                },
                            text = listView.name,
                            selected = selected,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractionOption(
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

internal val PostInteractionSetting.label: String
    @Composable get() {
        return if (this.replySetting is ReplySetting.Everybody) {
            stringResource(LocalizedString.sharedPublishInteractionNoLimit)
        } else {
            stringResource(LocalizedString.sharedPublishInteractionLimited)
        }
    }

internal val PostInteractionSetting.labelIcon: ImageVector
    @Composable get() {
        return if (this.replySetting is ReplySetting.Everybody) {
            Icons.Default.Public
        } else {
            Icons.Outlined.Group
        }
    }
