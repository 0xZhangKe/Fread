package com.zhangke.utopia.activitypubapp.uri

import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubUriValidateUseCase @Inject constructor(
    private val parseUriToActivityPubUriUseCase: ParseUriToActivityPubUriUseCase,
) {

    operator fun invoke(uri: StatusProviderUri): Boolean {
        return parseUriToActivityPubUriUseCase(uri) != null
    }
}
