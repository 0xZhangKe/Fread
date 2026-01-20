package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.GetProfileQueryParams
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.PlatformLocator
import sh.christian.ozone.api.Did

class UnblockUserWithoutUriUseCase(
    private val clientManager: BlueskyClientManager,
    private val updateBlock: UpdateBlockUseCase,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        author: BlogAuthor,
    ): Result<Unit> {
        val client = clientManager.getClient(locator)
        val did = author.webFinger.did
        if (did.isNullOrEmpty()) return Result.failure(IllegalArgumentException("Author did is empty"))
        val profile = client.getProfileCatching(GetProfileQueryParams(Did(did)))
            .getOrNull()
        if (profile == null) {
            return Result.failure(IllegalArgumentException("Failed to get profile for did: $did"))
        }
        val blockingUri = profile.viewer?.blocking
        if (blockingUri == null) {
            return Result.success(Unit)
        }
        return updateBlock(
            locator = locator,
            did = did,
            block = false,
            blockUri = blockingUri.toString()
        ).map { }
    }
}
