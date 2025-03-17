package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion
import app.bsky.actor.PutPreferencesRequest
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class UpdatePreferencesUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        updater: (List<PreferencesUnion>) -> List<PreferencesUnion>,
    ): Result<Unit> {
        val client = clientManager.getClient(role)
        val preferenceResult = client.getPreferencesCatching()
        if (preferenceResult.isFailure) {
            return Result.failure(preferenceResult.exceptionOrThrow())
        }
        val preference = preferenceResult.getOrThrow()
        val request = PutPreferencesRequest(
            preferences = updater(preference.preferences),
        )
        return clientManager.getClient(role).putPreferencesCatching(request)
    }
}
