package com.zhangke.utopia.commonbiz.shared.blog.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.ktx.launchInScreenModel
import com.zhangke.krouter.KRouter
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class BlogDetailViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ScreenModel {

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    fun onUserInfoClick(author: BlogAuthor) {
        launchInScreenModel {
            statusProvider.screenProvider
                .getUserDetailRoute(IdentityRole.nonIdentityRole, author.uri)
                ?.let { KRouter.route<Screen>(it) }
                ?.let { _openScreenFlow.emit(it) }
        }
    }
}