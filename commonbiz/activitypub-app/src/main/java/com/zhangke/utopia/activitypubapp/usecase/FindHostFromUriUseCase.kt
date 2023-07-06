package com.zhangke.utopia.activitypubapp.usecase

import com.zhangke.utopia.activitypubapp.uri.timeline.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

class FindHostFromUriUseCase @Inject constructor(
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
    private val parseUriToTimelineUriUseCase: ParseUriToTimelineUriUseCase,
) {

    operator fun invoke(uri: String): String? {
        val activityPubUri = StatusProviderUri.create(uri) ?: return null
        parseUriToUserUriUseCase(activityPubUri)?.let { return it.finger.host }
        parseUriToTimelineUriUseCase(activityPubUri)?.let { return it.timelineServerHost }
        return null
    }
}
