package com.zhangke.fread.commonbiz.shared.blog.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class BlogDetailViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    fun onUserInfoClick(author: BlogAuthor) {
        viewModelScope.launch {
            statusProvider.screenProvider
                .getUserDetailScreen(IdentityRole.nonIdentityRole, author.uri)
                ?.let { _openScreenFlow.emit(it) }
        }
    }
}
