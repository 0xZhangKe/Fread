package com.zhangke.fread.bluesky.internal.usecase

import com.atproto.repo.DeleteRecordRequest
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.PlatformLocator
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Nsid
import sh.christian.ozone.api.RKey

class DeleteRecordUseCase(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        collection: Nsid,
        rkey: RKey,
    ): Result<Unit> {
        val repo = clientManager.getClient(locator).loggedAccountProvider()
            ?.did?.let { Did(it) }
            ?: return Result.failure(IllegalStateException("No logged account"))
        return clientManager.getClient(locator)
            .deleteRecordCatching(
                DeleteRecordRequest(
                    repo = repo,
                    collection = collection,
                    rkey = rkey,
                )
            ).map { }
    }
}
