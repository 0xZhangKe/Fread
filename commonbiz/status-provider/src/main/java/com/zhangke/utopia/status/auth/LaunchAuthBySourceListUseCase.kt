package com.zhangke.utopia.status.auth

import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class LaunchAuthBySourceListUseCase @Inject constructor(
    private val launchers: Set<@JvmSuppressWildcards AuthWithStatusSourceLauncher>,
) {

    suspend operator fun invoke(source: StatusSource): Result<Boolean> {
        val launcher = launchers.firstOrNull { it.applicable(source) }
            ?: return Result.failure(IllegalArgumentException("Illegal source: $source"))
        return launcher.launch(source)
    }
}

interface AuthWithStatusSourceLauncher {

    fun applicable(source: StatusSource): Boolean

    suspend fun launch(source: StatusSource): Result<Boolean>
}
