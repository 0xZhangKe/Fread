package com.zhangke.utopia.pages.feeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.toast.toast
import com.zhangke.utopia.status_provider.BlogProviderManager
import com.zhangke.utopia.status_provider.Status
import com.zhangke.utopia.status_provider.db.BlogSourceRepo
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
            val provider = BlogSourceRepo.queryFeedsById(feedsId)
                ?.sourceList
                ?.firstOrNull()
                ?.let {
                    BlogProviderManager.providerFactoryList
                        .mapFirst { factory -> factory.createProvider(it) }
                } ?: return@launch
            provider.requestStatuses()
                .onSuccess {
                    feeds.emit(it)
                }
                .onFailure {
                    toast(it.message)
                }
        }
    }
}