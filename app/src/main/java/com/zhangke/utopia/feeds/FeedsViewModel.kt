package com.zhangke.utopia.feeds

import androidx.lifecycle.ViewModel
import com.zhangke.utopia.blogprovider.Status
import kotlinx.coroutines.flow.MutableSharedFlow

class FeedsViewModel : ViewModel() {

    val feeds = MutableSharedFlow<List<Status>>(1)

    init {

    }
}