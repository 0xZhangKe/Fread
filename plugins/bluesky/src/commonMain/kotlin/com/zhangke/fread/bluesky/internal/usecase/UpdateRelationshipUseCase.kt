package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.GetProfileQueryParams
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.adjustToRkey
import com.zhangke.fread.bluesky.internal.client.followRecord
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class UpdateRelationshipUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val createRecord: CreateRecordUseCase,
    private val deleteRecord: DeleteRecordUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        targetDid: String,
        type: UpdateRelationshipType,
        followUri: String? = null,
    ): Result<Unit> {
        val did = Did(targetDid)
        when (type) {
            UpdateRelationshipType.FOLLOW -> {
                return createRecord(
                    role = role,
                    repo = did,
                    collection = BskyCollections.follow,
                    record = followRecord(targetDid),
                )
            }

            UpdateRelationshipType.UNFOLLOW -> {
                val finalFollowUri = if (followUri.isNullOrEmpty()) {
                    val profileResult = clientManager.getClient(role)
                        .getProfileCatching(GetProfileQueryParams(did))
                    if (profileResult.isFailure) return Result.failure(profileResult.exceptionOrNull()!!)
                    profileResult.getOrThrow().viewer?.following?.atUri
                } else {
                    followUri
                }
                if (finalFollowUri.isNullOrEmpty()) return Result.success(Unit)
                return deleteRecord(
                    role = role,
                    repo = did,
                    collection = BskyCollections.follow,
                    rkey = finalFollowUri.adjustToRkey(),
                )
            }
        }
    }
}

enum class UpdateRelationshipType {

    FOLLOW,
    UNFOLLOW,
}
