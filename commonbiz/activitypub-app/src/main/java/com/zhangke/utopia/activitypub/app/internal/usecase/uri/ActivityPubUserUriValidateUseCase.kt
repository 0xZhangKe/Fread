package com.zhangke.utopia.activitypub.app.internal.usecase.uri

import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubUserUriValidateUseCase @Inject constructor(
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
) {

    operator fun invoke(uri: StatusProviderUri): Boolean {
        return parseUriToUserUriUseCase(uri) != null
    }
}
