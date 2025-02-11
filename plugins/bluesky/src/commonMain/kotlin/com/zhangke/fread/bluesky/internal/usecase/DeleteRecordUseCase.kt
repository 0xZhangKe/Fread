package com.zhangke.fread.bluesky.internal.usecase

import com.atproto.repo.DeleteRecordRequest
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtIdentifier
import sh.christian.ozone.api.Nsid

class DeleteRecordUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        repo: AtIdentifier,
        collection: Nsid,
        rkey: String,
    ): Result<Unit>{
        return clientManager.getClient(role)
            .deleteRecordCatching(
                DeleteRecordRequest(
                    repo = repo,
                    collection = collection,
                    rkey = rkey,
                )
            ).map {  }
    }
}
