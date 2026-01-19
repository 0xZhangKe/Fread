package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.Profile
import com.atproto.repo.GetRecordQueryParams
import com.atproto.repo.PutRecordRequest
import com.atproto.repo.PutRecordResponse
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.selfRkey
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import sh.christian.ozone.api.Did

class UpdateProfileRecordUseCase() {

    suspend operator fun invoke(
        client: BlueskyClient,
        updater: (Profile) -> Profile,
    ): Result<PutRecordResponse> {
        val account =
            client.loggedAccountProvider() ?: return Result.failure(Exception("No logged account"))
        val did = Did(account.did)
        val recordResult = client.getRecordCatching(
            GetRecordQueryParams(
                repo = did, collection = BskyCollections.profile, rkey = selfRkey,
            )
        )
        if (recordResult.isFailure) return Result.failure(recordResult.exceptionOrThrow())
        val record = recordResult.getOrThrow()
        val profile: Profile = record.bskyJson()
        val newProfile = updater(profile)
        return client.putRecordCatching(
            PutRecordRequest(
                repo = did,
                collection = BskyCollections.profile,
                rkey = selfRkey,
                record = newProfile.bskyJson(),
                swapRecord = record.cid,
            )
        )
    }
}
