package com.zhangke.utopia.activitypub.app

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.status.IStatusProvider
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.emoji.CustomEmojiProvider
import com.zhangke.utopia.status.emoji.ICustomEmojiProvider
import com.zhangke.utopia.status.status.IStatusResolver
import javax.inject.Inject

@Filt
class ActivityPubProvider @Inject constructor(
    internalPlatformResolver: ActivityPubPlatformResolver,
    internalSearchEngine: ActivityPubSearchEngine,
    internalStatusResolver: ActivityPubStatusResolver,
    internalSourceResolver: ActivityPubSourceResolver,
    internalAccountManager: ActivityPubAccountManager,
    internalCustomEmojiProvider: ActivityPubCustomEmojiProvider,
) : IStatusProvider {

    override val platformResolver = internalPlatformResolver

    override val searchEngine = internalSearchEngine

    override val statusResolver: IStatusResolver = internalStatusResolver

    override val statusSourceResolver = internalSourceResolver

    override val accountManager: IAccountManager = internalAccountManager

    override val customEmojiProvider: ICustomEmojiProvider = internalCustomEmojiProvider
}