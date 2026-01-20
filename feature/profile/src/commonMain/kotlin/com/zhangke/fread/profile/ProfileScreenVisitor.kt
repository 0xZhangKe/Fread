package com.zhangke.fread.profile

import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.commonbiz.shared.IProfileScreenVisitor
import com.zhangke.fread.profile.screen.donate.DonateScreenNavKey

class ProfileScreenVisitor : IProfileScreenVisitor {

    override fun getDonateScreen(): NavKey {
        return DonateScreenNavKey
    }
}
