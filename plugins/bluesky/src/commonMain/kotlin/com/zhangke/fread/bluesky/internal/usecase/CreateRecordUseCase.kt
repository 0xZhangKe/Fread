package com.zhangke.fread.bluesky.internal.usecase

import com.atproto.repo.CreateRecordRequest
import com.atproto.repo.CreateRecordResponse
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Nsid
import sh.christian.ozone.api.model.JsonContent

class CreateRecordUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        collection: Nsid,
        record: JsonContent,
    ): Result<CreateRecordResponse> {
        val repo = clientManager.getClient(role).loggedAccountProvider()
            ?.did?.let { Did(it) }
            ?: return Result.failure(IllegalStateException("No logged account"))
        return clientManager.getClient(role)
            .createRecordCatching(
                CreateRecordRequest(
                    repo = repo,
                    collection = collection,
                    record = record,
                )
            )
    }
}
