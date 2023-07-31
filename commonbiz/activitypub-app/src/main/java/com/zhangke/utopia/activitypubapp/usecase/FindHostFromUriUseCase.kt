package com.zhangke.utopia.activitypubapp.usecase

import com.zhangke.utopia.activitypubapp.uri.timeline.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class FindHostFromUriUseCase @Inject constructor(
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
) {

    operator fun invoke(uri: StatusProviderUri): String? {
        parseUriToUserUriUseCase(uri)?.let { return it.finger.host }
        parseUriToTimelineUriUseCase(uri)?.let { return it.timelineServerHost }
        return null
    }
}
