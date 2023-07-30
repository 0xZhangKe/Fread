package com.zhangke.utopia.activitypubapp.uri

import com.zhangke.utopia.activitypubapp.uri.timeline.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ParseUriToActivityPubUriUseCase @Inject constructor(
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
) {

    operator fun invoke(uri: StatusProviderUri): ActivityPubUri? {
        parseUriToUserUriUseCase(uri)?.let { return it }
        parseUriToTimelineUriUseCase(uri)?.let { return it }
        return null
    }
}
