package com.zhangke.fread.profile.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.common.handler.LocalTextHandler
import com.zhangke.fread.common.language.LanguageSettingItem
import com.zhangke.fread.common.language.LocalActivityLanguageHelper
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.ic_code
import com.zhangke.fread.feature.profile.ic_ratting
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.profile.screen.donate.DonateScreenNavKey
import com.zhangke.fread.profile.screen.opensource.OpenSourceScreenNavKey
import com.zhangke.fread.profile.screen.setting.about.AboutScreenNavKey
import com.zhangke.fread.profile.screen.setting.appearance.AppearanceSettingsNavKey
import com.zhangke.fread.profile.screen.setting.behavior.BehaviorSettingsNavKey
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Serializable
object SettingScreenNavKey : NavKey

private val itemHeight = 82.dp

@Composable
fun SettingScreen(viewModel: SettingScreenModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()

    val activityLanguageHelper = LocalActivityLanguageHelper.current
    val activityTextHandler = LocalTextHandler.current

    SettingContent(
        uiState = uiState,
        onBackClick = backStack::removeLastOrNull,
        onOpenSourceClick = {
            backStack.add(OpenSourceScreenNavKey)
        },
        onLanguageClick = {
            activityLanguageHelper.setLanguage(it)
        },
        onRatingClick = {
            activityTextHandler.openAppMarket()
        },
        onAppearanceClick = {
            backStack.add(AppearanceSettingsNavKey)
        },
        onBehaviorClick = {
            backStack.add(BehaviorSettingsNavKey)
        },
        onAboutClick = {
            backStack.add(AboutScreenNavKey)
        },
        onDonateClick = {
            backStack.add(DonateScreenNavKey)
        },
    )
}

@Composable
private fun SettingContent(
    uiState: SettingUiState,
    onBackClick: () -> Unit,
    onOpenSourceClick: () -> Unit,
    onLanguageClick: (LanguageSettingItem) -> Unit,
    onRatingClick: () -> Unit,
    onAboutClick: () -> Unit,
    onDonateClick: () -> Unit,
    onAppearanceClick: () -> Unit,
    onBehaviorClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.settings),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingItem(
                icon = Icons.Default.Palette,
                title = stringResource(LocalizedString.setting_group_appearance),
                subtitle = null,
                onClick = onAppearanceClick,
            )
            SettingItem(
                icon = Icons.Default.ViewTimeline,
                title = stringResource(LocalizedString.setting_group_behavior),
                subtitle = null,
                onClick = onBehaviorClick,
            )
            LanguageItem(
                onLanguageClick = onLanguageClick,
            )
            FeedbackItem()
            SettingItem(
                icon = vectorResource(Res.drawable.ic_code),
                title = stringResource(LocalizedString.profileSettingOpenSourceTitle),
                subtitle = stringResource(LocalizedString.profileSettingOpenSourceDesc),
                onClick = onOpenSourceClick,
            )
            SettingItem(
                icon = {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(2.dp),
                        imageVector = vectorResource(Res.drawable.ic_ratting),
                        contentDescription = stringResource(LocalizedString.profileSettingRatting),
                    )
                },
                title = stringResource(LocalizedString.profileSettingRatting),
                subtitle = stringResource(LocalizedString.profileSettingRattingDesc),
                onClick = onRatingClick,
            )
            SettingItem(
                icon = Icons.Outlined.Coffee,
                title = stringResource(LocalizedString.donate),
                subtitle = stringResource(LocalizedString.profileSettingDonateDesc),
                onClick = onDonateClick,
            )
            SettingItem(
                icon = Icons.Outlined.Info,
                title = stringResource(LocalizedString.profileSettingAboutTitle),
                subtitle = uiState.settingInfo,
                redDot = uiState.haveNewAppVersion,
                onClick = onAboutClick,
            )
        }
    }
}

@Composable
private fun LanguageItem(
    onLanguageClick: (LanguageSettingItem) -> Unit,
) {
    val activityLanguageHelper = LocalActivityLanguageHelper.current
    val subtitle = activityLanguageHelper.currentLanguage.getDisplayName()
    SettingItemWithPopup(
        icon = Icons.Default.Language,
        title = stringResource(LocalizedString.profileSettingLanguageTitle),
        subtitle = subtitle,
        dropDownItemCount = LanguageSettingItem.items.size,
        dropDownItemText = { LanguageSettingItem.items[it].getDisplayName() },
        onItemClick = {
            onLanguageClick(LanguageSettingItem.items[it])
        }
    )
}

@Composable
private fun FeedbackItem() {
    var showFeedbackBottomSheet by remember {
        mutableStateOf(false)
    }
    SettingItem(
        icon = Icons.AutoMirrored.Outlined.Chat,
        title = stringResource(LocalizedString.profileSettingOpenSourceFeedback),
        subtitle = stringResource(LocalizedString.profileSettingOpenSourceFeedbackDesc),
        onClick = {
            showFeedbackBottomSheet = true
        },
    )
    if (showFeedbackBottomSheet) {
        FeedbackBottomSheet(
            onDismissRequest = { showFeedbackBottomSheet = false },
        )
    }
}
