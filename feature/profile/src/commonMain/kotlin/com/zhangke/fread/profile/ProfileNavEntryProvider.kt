package com.zhangke.fread.profile

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.profile.screen.donate.DonateScreen
import com.zhangke.fread.profile.screen.donate.DonateScreenNavKey
import com.zhangke.fread.profile.screen.opensource.OpenSourceScreen
import com.zhangke.fread.profile.screen.opensource.OpenSourceScreenNavKey
import com.zhangke.fread.profile.screen.setting.SettingScreen
import com.zhangke.fread.profile.screen.setting.SettingScreenNavKey
import com.zhangke.fread.profile.screen.setting.about.AboutScreen
import com.zhangke.fread.profile.screen.setting.about.AboutScreenNavKey
import com.zhangke.fread.profile.screen.setting.appearance.AppearanceSettingsNavKey
import com.zhangke.fread.profile.screen.setting.appearance.AppearanceSettingsScreen
import com.zhangke.fread.profile.screen.setting.behavior.BehaviorSettingsNavKey
import com.zhangke.fread.profile.screen.setting.behavior.BehaviorSettingsScreen
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel

class ProfileNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<SettingScreenNavKey> {
            SettingScreen(koinViewModel())
        }
        entry<AboutScreenNavKey> {
            AboutScreen(koinViewModel())
        }
        entry<AppearanceSettingsNavKey> {
            AppearanceSettingsScreen(koinViewModel())
        }
        entry<BehaviorSettingsNavKey> {
            BehaviorSettingsScreen(koinViewModel())
        }
        entry<OpenSourceScreenNavKey> {
            OpenSourceScreen()
        }
        entry<DonateScreenNavKey> {
            DonateScreen()
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(SettingScreenNavKey::class)
        subclass(AboutScreenNavKey::class)
        subclass(AppearanceSettingsNavKey::class)
        subclass(BehaviorSettingsNavKey::class)
        subclass(OpenSourceScreenNavKey::class)
        subclass(DonateScreenNavKey::class)
    }
}
