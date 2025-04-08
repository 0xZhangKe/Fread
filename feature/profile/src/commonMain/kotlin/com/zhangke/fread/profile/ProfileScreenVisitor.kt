package com.zhangke.fread.profile

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.commonbiz.shared.IProfileScreenVisitor
import com.zhangke.fread.profile.screen.donate.DonateScreen

class ProfileScreenVisitor : IProfileScreenVisitor {

    override fun getDonateScreen(): Screen {
        return DonateScreen()
    }
}
