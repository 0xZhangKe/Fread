package com.zhangke.fread.profile.screen.setting

import androidx.compose.runtime.Composable
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.language.LanguageSettingType
import com.zhangke.fread.common.theme.ThemeType
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.profile_setting_dark_mode_dark
import com.zhangke.fread.feature.profile.profile_setting_dark_mode_follow_system
import com.zhangke.fread.feature.profile.profile_setting_dark_mode_light
import com.zhangke.fread.feature.profile.profile_setting_font_size_large
import com.zhangke.fread.feature.profile.profile_setting_font_size_medium
import com.zhangke.fread.feature.profile.profile_setting_font_size_small
import com.zhangke.fread.feature.profile.profile_setting_language_en
import com.zhangke.fread.feature.profile.profile_setting_language_system
import com.zhangke.fread.feature.profile.profile_setting_language_zh
import com.zhangke.fread.feature.profile.profile_setting_theme_default
import com.zhangke.fread.feature.profile.profile_setting_theme_system
import com.zhangke.fread.feature.profile.profile_setting_timeline_position_last_read
import com.zhangke.fread.feature.profile.profile_setting_timeline_position_newest
import org.jetbrains.compose.resources.stringResource

val LanguageSettingType.typeName: String
    @Composable
    get() {
        return when (this) {
            LanguageSettingType.CN -> stringResource(Res.string.profile_setting_language_zh)
            LanguageSettingType.EN -> stringResource(Res.string.profile_setting_language_en)
            LanguageSettingType.SYSTEM -> stringResource(Res.string.profile_setting_language_system)
        }
    }

val DayNightMode.modeName: String
    @Composable
    get() {
        return when (this) {
            DayNightMode.NIGHT -> stringResource(Res.string.profile_setting_dark_mode_dark)
            DayNightMode.DAY -> stringResource(Res.string.profile_setting_dark_mode_light)
            DayNightMode.FOLLOW_SYSTEM -> stringResource(Res.string.profile_setting_dark_mode_follow_system)
        }
    }

val StatusContentSize.sizeName: String
    @Composable
    get() = when (this) {
        StatusContentSize.SMALL -> stringResource(Res.string.profile_setting_font_size_small)
        StatusContentSize.MEDIUM -> stringResource(Res.string.profile_setting_font_size_medium)
        StatusContentSize.LARGE -> stringResource(Res.string.profile_setting_font_size_large)
    }

val TimelineDefaultPosition.displayName: String
    @Composable
    get() = when (this) {
        TimelineDefaultPosition.NEWEST -> stringResource(Res.string.profile_setting_timeline_position_newest)
        TimelineDefaultPosition.LAST_READ -> stringResource(Res.string.profile_setting_timeline_position_last_read)
    }

val ThemeType.displayName: String
    @Composable
    get() = when (this) {
        ThemeType.DEFAULT -> stringResource(Res.string.profile_setting_theme_default)
        ThemeType.SYSTEM_DYNAMIC -> stringResource(Res.string.profile_setting_theme_system)
    }
