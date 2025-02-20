package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.lifecycle.ViewModel
import app.bsky.graph.GetListsQueryParams
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class PublishPostViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val role: IdentityRole,
): ViewModel() {


    init {

    }

    private fun loadUserList(){
        launchInViewModel{
            val client = clientManager.getClient(role)
            val account = client.loggedAccountProvider() ?: return@launchInViewModel
            client.getListsCatching(
                GetListsQueryParams(
                    actor = Did(account.did),
                )
            )
        }
    }
}
