package com.zhangke.fread.profile

import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.commonbiz.shared.IProfileScreenVisitor
import com.zhangke.fread.profile.screen.donate.DonateScreenNavKey
import com.zhangke.fread.profile.screen.setting.SettingScreenNavKey

class ProfileScreenVisitor : IProfileScreenVisitor {

    override fun getDonateScreen(): NavKey {
        return DonateScreenNavKey
    }

    override fun getSettingScreenNavKey(): NavKey = SettingScreenNavKey
}
