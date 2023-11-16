package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.uri.ParseUriToUserUriUseCase
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
