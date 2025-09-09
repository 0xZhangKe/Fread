package com.zhangke.fread.profile.screen.setting

import androidx.compose.runtime.Composable
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.language.LanguageSettingType
import com.zhangke.fread.common.theme.ThemeType
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

val LanguageSettingType.typeName: String
    @Composable
    get() {
        return when (this) {
            LanguageSettingType.CN -> stringResource(LocalizedString.profileSettingLanguageZh)
            LanguageSettingType.EN -> stringResource(LocalizedString.profileSettingLanguageEn)
            LanguageSettingType.SYSTEM -> stringResource(LocalizedString.profileSettingLanguageSystem)
        }
    }

val DayNightMode.modeName: String
    @Composable
    get() {
        return when (this) {
            DayNightMode.NIGHT -> stringResource(LocalizedString.profileSettingDarkModeDark)
            DayNightMode.DAY -> stringResource(LocalizedString.profileSettingDarkModeLight)
            DayNightMode.FOLLOW_SYSTEM -> stringResource(LocalizedString.profileSettingDarkModeFollowSystem)
        }
    }

val StatusContentSize.sizeName: String
    @Composable
    get() = when (this) {
        StatusContentSize.SMALL -> stringResource(LocalizedString.profileSettingFontSizeSmall)
        StatusContentSize.MEDIUM -> stringResource(LocalizedString.profileSettingFontSizeMedium)
        StatusContentSize.LARGE -> stringResource(LocalizedString.profileSettingFontSizeLarge)
    }

val TimelineDefaultPosition.displayName: String
    @Composable
    get() = when (this) {
        TimelineDefaultPosition.NEWEST -> stringResource(LocalizedString.profileSettingTimelinePositionNewest)
        TimelineDefaultPosition.LAST_READ -> stringResource(LocalizedString.profileSettingTimelinePositionLastRead)
    }

val ThemeType.displayName: String
    @Composable
    get() = when (this) {
        ThemeType.DEFAULT -> stringResource(LocalizedString.profileSettingThemeDefault)
        ThemeType.SYSTEM_DYNAMIC -> stringResource(LocalizedString.profileSettingThemeSystem)
    }
