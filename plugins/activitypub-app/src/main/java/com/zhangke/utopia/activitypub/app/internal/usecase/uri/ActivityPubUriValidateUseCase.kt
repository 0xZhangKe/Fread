package com.zhangke.utopia.activitypub.app.internal.usecase.uri

import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubUriValidateUseCase @Inject constructor(
    private val parseUriToActivityPubUriUseCase: ParseUriToActivityPubUriUseCase,
) {

    operator fun invoke(uri: StatusProviderUri): Boolean {
        return parseUriToActivityPubUriUseCase(uri) != null
    }
}
