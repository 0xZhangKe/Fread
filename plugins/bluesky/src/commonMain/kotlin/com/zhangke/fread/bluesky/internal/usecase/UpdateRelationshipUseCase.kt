package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.GetProfileQueryParams
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.adjustToRkey
import com.zhangke.fread.bluesky.internal.client.followRecord
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Did

class UpdateRelationshipUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val createRecord: CreateRecordUseCase,
    private val deleteRecord: DeleteRecordUseCase,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        targetDid: String,
        type: UpdateRelationshipType,
        followUri: String? = null,
    ): Result<AtUri?> {
        val did = Did(targetDid)
        when (type) {
            UpdateRelationshipType.FOLLOW -> {
                return createRecord(
                    locator = locator,
                    collection = BskyCollections.follow,
                    record = followRecord(targetDid),
                ).map { it.uri }
            }

            UpdateRelationshipType.UNFOLLOW -> {
                val finalFollowUri = if (followUri.isNullOrEmpty()) {
                    val profileResult = clientManager.getClient(locator)
                        .getProfileCatching(GetProfileQueryParams(did))
                    if (profileResult.isFailure) return Result.failure(profileResult.exceptionOrNull()!!)
                    profileResult.getOrThrow().viewer?.following?.atUri
                } else {
                    followUri
                }
                if (finalFollowUri.isNullOrEmpty()) return Result.success(null)
                return deleteRecord(
                    locator = locator,
                    collection = BskyCollections.follow,
                    rkey = finalFollowUri.adjustToRkey(),
                ).map { null }
            }
        }
    }
}

enum class UpdateRelationshipType {

    FOLLOW,
    UNFOLLOW,
}
