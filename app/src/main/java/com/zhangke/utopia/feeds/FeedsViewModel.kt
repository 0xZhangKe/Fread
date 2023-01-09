package com.zhangke.utopia.feeds

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.providers.ActivityPubProviderFactory
import com.zhangke.utopia.blogprovider.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class FeedsViewModel : ViewModel() {

    val feeds = MutableSharedFlow<List<Status>>(1)

    init {
        viewModelScope.launch {
            val provider = ActivityPubProviderFactory.createTimelineProvider("m.cmx.im")
            provider.requestStatuses()
                .onSuccess {
                    feeds.emit(it)
                }
                .onFailure {
                    Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}