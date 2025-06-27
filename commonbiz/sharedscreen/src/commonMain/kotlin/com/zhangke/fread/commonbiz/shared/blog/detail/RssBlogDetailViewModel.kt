package com.zhangke.fread.commonbiz.shared.blog.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.BlogAuthor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class RssBlogDetailViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    fun onUserInfoClick(author: BlogAuthor) {
        viewModelScope.launch {
            statusProvider.screenProvider
                .getUserDetailScreenWithoutAccount(author.uri)
                ?.let { _openScreenFlow.emit(it) }
        }
    }
}
