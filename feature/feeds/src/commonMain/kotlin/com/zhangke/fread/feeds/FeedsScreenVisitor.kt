package com.zhangke.fread.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.commonbiz.shared.IFeedsScreenVisitor
import com.zhangke.fread.feeds.pages.manager.add.pre.PreAddFeedsScreen
import me.tatarka.inject.annotations.Inject

class FeedsScreenVisitor @Inject constructor() : IFeedsScreenVisitor {

    override fun getAddContentScreen(): Screen {
        return PreAddFeedsScreen()
    }
}
