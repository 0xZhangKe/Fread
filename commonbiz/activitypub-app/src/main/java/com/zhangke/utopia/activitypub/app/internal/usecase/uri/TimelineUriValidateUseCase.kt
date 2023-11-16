package com.zhangke.utopia.activitypub.app.internal.usecase.uri

import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class TimelineUriValidateUseCase @Inject constructor(
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
) {

    operator fun invoke(uri: StatusProviderUri): Boolean {
        return parseUriToTimelineUriUseCase(uri) != null
    }
}
