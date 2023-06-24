package com.zhangke.utopia.activitypubapp.uri.timeline

import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

class TimelineUriValidateUseCase @Inject constructor(
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
) {

    operator fun invoke(uri: StatusProviderUri): Boolean {
        return parseUriToTimelineUriUseCase(uri) != null
    }
}
