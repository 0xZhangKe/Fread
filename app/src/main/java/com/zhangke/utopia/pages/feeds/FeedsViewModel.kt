package com.zhangke.utopia.pages.feeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.utopia.status.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class FeedsViewModel : ViewModel() {

    val feeds = MutableSharedFlow<List<Status>>(1)

    var feedsId by Delegates.notNull<Int>()

    init {
        fetchFeeds()
    }

    private fun fetchFeeds() {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }
}