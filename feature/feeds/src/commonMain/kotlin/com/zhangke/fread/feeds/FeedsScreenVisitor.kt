package com.zhangke.fread.feeds

import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.commonbiz.shared.IFeedsScreenVisitor
import com.zhangke.fread.feeds.pages.manager.add.type.SelectContentTypeScreenNavKey
import me.tatarka.inject.annotations.Inject

class FeedsScreenVisitor @Inject constructor() : IFeedsScreenVisitor {

    override fun getAddContentScreen(): NavKey {
        return SelectContentTypeScreenNavKey
    }
}
