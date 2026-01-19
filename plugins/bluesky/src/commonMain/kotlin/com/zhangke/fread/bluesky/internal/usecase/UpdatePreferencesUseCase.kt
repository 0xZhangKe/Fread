package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion
import app.bsky.actor.PutPreferencesRequest
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.PlatformLocator

class UpdatePreferencesUseCase (
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        updater: (List<PreferencesUnion>) -> List<PreferencesUnion>,
    ): Result<Unit> {
        val client = clientManager.getClient(locator)
        val preferenceResult = client.getPreferencesCatching()
        if (preferenceResult.isFailure) {
            return Result.failure(preferenceResult.exceptionOrThrow())
        }
        val preference = preferenceResult.getOrThrow()
        val request = PutPreferencesRequest(
            preferences = updater(preference.preferences),
        )
        return clientManager.getClient(locator).putPreferencesCatching(request)
    }
}