package com.zhangke.utopia.status.domain

import javax.inject.Inject

class SourceAuthorizationValidateUseCase @Inject constructor() {

    suspend operator fun invoke(sourceUri: String): Boolean {
        return false
    }
}
