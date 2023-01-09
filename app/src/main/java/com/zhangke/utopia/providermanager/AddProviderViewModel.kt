package com.zhangke.utopia.providermanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.framework.utils.RegexFactory
import com.zhangke.utopia.activitypubapp.servers.ActivityPubServers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddProviderViewModel : ViewModel() {

    private val _pageState = MutableStateFlow(PageState.INITIALIZE)

    /**
     * - 0: Initialized page.
     * - 1: ActivityPub info.
     */
    val pageState: StateFlow<PageState> get() = _pageState

    fun onAddClick(content: String) {
        viewModelScope.launch {
            if (addServerAsActivityPub(content)) return@launch

        }
    }

    private var activityPubInstance: ActivityPubInstance? = null

    private suspend fun addServerAsActivityPub(content: String): Boolean {
        val domain = RegexFactory.getDomainRegex().find(content)?.groups?.first()?.value
        if (domain.isNullOrEmpty()) return false
        activityPubInstance =
            ActivityPubServers.getServerInstance(domain).getOrNull() ?: return false
        _pageState.emit(PageState.ACTIVITY_PUB_INFO)
        return true
    }

    fun requireActivityPubInstance(): ActivityPubInstance {
        return activityPubInstance!!
    }

    fun moveToInitializedPage() {
        viewModelScope.launch {
            _pageState.emit(PageState.INITIALIZE)
        }
    }

    fun onAddActivityPubServer(){
        val instance = activityPubInstance!!

    }

    enum class PageState {

        INITIALIZE,
        ACTIVITY_PUB_INFO,
    }
}